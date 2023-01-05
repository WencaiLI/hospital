package com.thtf.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thtf.common.dto.adminserver.UserInfo;
import com.thtf.common.entity.adminserver.TblBasicData;
import com.thtf.common.entity.adminserver.TblUser;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.common.security.SecurityContextHolder;
import com.thtf.common.util.IdGeneratorSnowflake;
import com.thtf.office.common.util.HttpUtil;
import com.thtf.office.dto.converter.VehicleSchedulingConverter;
import com.thtf.office.mapper.TblVehicleInfoMapper;
import com.thtf.office.vo.VehicleSchedulingParamVO;
import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.mapper.TblVehicleSchedulingMapper;
import com.thtf.office.service.TblVehicleSchedulingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.office.vo.VehicleSelectByDateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * <p>
 * 车辆调度表 服务实现类
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
@Service
@Slf4j
public class TblVehicleSchedulingServiceImpl extends ServiceImpl<TblVehicleSchedulingMapper, TblVehicleScheduling> implements TblVehicleSchedulingService {

    @Resource
    private TblVehicleSchedulingMapper vehicleSchedulingMapper;

    @Resource
    private TblVehicleInfoMapper vehicleInfoMapper;

    @Resource
    private VehicleSchedulingConverter vehicleSchedulingConverter;

    @Autowired
    private IdGeneratorSnowflake idGeneratorSnowflake;

    @Autowired
    private AdminAPI adminAPI;

    /**
     * @Author: liwencai
     * @Description: 新增调度信息
     * @Date: 2022/7/27
     * @Param paramVO:
     * @return: boolean
     */
    @Override
    @Transactional
    public Map<String, Object> insert(VehicleSchedulingParamVO paramVO) {

        /* 查询这么一种调度：它的调度时间处于新增调度之间 */
        QueryWrapper<TblVehicleScheduling> queryWrapper_1 = new QueryWrapper<>();
        queryWrapper_1.isNull("delete_time").eq("car_number",paramVO.getCarNumber()).ge("start_time",paramVO.getStartTime()).le("end_time",paramVO.getEndTime());
        List<TblVehicleScheduling> conflictSchedulingList_1 = vehicleSchedulingMapper.selectList(queryWrapper_1);

        if(conflictSchedulingList_1.size() > 0){
            log.warn("存在调度处于调度时间段之间，调度时间冲突");
            return getServiceResultMap("error","存在调度处于调度时间段之间，调度时间冲突",null);
        }

        /* 查询这么两种情况的调度：
         * 第一种；新增的调度的开始时间处于它的调度时间范围内；
         * 第二中调度：新增的调度的结束时间出它的调度时间内
         */
        QueryWrapper<TblVehicleScheduling> queryWrapper_2 = new QueryWrapper<>();
        queryWrapper_2.isNull("delete_time").eq("car_number",paramVO.getCarNumber())
                .and(x->x.and(e->e.lt("start_time",paramVO.getStartTime()).gt("end_time",paramVO.getStartTime()))
                        .or(e->e.lt("start_time",paramVO.getEndTime()).gt("end_time",paramVO.getEndTime())));
        List<TblVehicleScheduling> conflictSchedulingList_2 = vehicleSchedulingMapper.selectList(queryWrapper_2);
        if(conflictSchedulingList_2.size() > 0){
            log.warn("新增的调度信息的开始时间或结束时间处于别的调度中间");
            return getServiceResultMap("error","新增的调度信息的开始时间或结束时间处于别的调度中间",null);
        }

        /* Bean映射 */
        TblVehicleScheduling scheduling = vehicleSchedulingConverter.toVehicleScheduling(paramVO);
        scheduling.setId(this.idGeneratorSnowflake.snowflakeId());
        scheduling.setCreateTime(LocalDateTime.now());
        scheduling.setCreateBy(getOperatorName());

        /* 如果该调度的开始时间小于系统时间，结束时间大于系统时间，将公车状态修改为出车中 */
        LocalDateTime nowTime= LocalDateTime.now();
        if(paramVO.getStartTime().isBefore(nowTime) && paramVO.getEndTime().isAfter(nowTime)){
            Map<String,Object> map = new HashMap<>();
            map.put("vid", paramVO.getVehicleInfoId());
            map.put("status",1);
            map.put("updateBy",getOperatorName());
            vehicleInfoMapper.changeVehicleStatus(map);
            log.warn("{}，{}，系统时间在两者之间所以，修改该公车状态为正在调度",paramVO.getStartTime(),paramVO.getEndTime());
            scheduling.setStatus(0);
        }
        /* 调度已经结束的调度 */
        if(paramVO.getStartTime().isBefore(nowTime) && paramVO.getEndTime().isBefore(nowTime)){
            scheduling.setStatus(1);
        }
        /* 尚未开始的调度 修改为待命中状态 todo 逻辑可能存在问题*/
        if(paramVO.getStartTime().isAfter(nowTime) && paramVO.getEndTime().isAfter(nowTime)){
            Map<String,Object> map = new HashMap<>();
            map.put("vid", paramVO.getVehicleInfoId());
            map.put("status",0);
            map.put("updateBy",getOperatorName());
            vehicleInfoMapper.changeVehicleStatus(map);
            scheduling.setStatus(2);
            log.warn("{}，{}，系统时间在两者之间所以，修改该公车状态为正在调度",paramVO.getStartTime(),paramVO.getEndTime());
        }
        // 计算改调度的秒数
        try {
            Long seconds = Math.abs(scheduling.getEndTime().until(scheduling.getStartTime(), ChronoUnit.SECONDS));
            scheduling.setWorkingDuration(seconds);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        if(vehicleSchedulingMapper.insert(scheduling) == 1){
            return getServiceResultMap("success",null,null);
        }else {
            return getServiceResultMap("error","新增调度信息失败",null);
        }
    }

    /**
     * @Author: liwencai
     * @Description: 根据id删除调度信息
     * @Date: 2022/7/27
     * @Param sid:
     * @return: boolean
     */
    @Override
    public boolean deleteById(Long sid) {
        LocalDateTime now = LocalDateTime.now();
        TblVehicleScheduling scheduling = vehicleSchedulingMapper.selectById(sid);
        if(null == scheduling){
            return false;
        }
        // 1 调度尚未结束时
        LocalDateTime startTime = scheduling.getStartTime();
        LocalDateTime endTime = scheduling.getEndTime();
        if (now.isBefore(endTime) &&now.isAfter(startTime)){
            Map<String,Object> map = new HashMap<>();
            map.put("vid",scheduling.getVehicleInfoId());
            map.put("status",0);
            map.put("updateBy",getOperatorName());
            // 修改公车状态为待命中
            vehicleInfoMapper.changeVehicleStatus(map);
        }
        // 2 调度已经结束时
        scheduling.setDeleteTime(LocalDateTime.now());
        scheduling.setDeleteBy(getOperatorName());
        QueryWrapper<TblVehicleScheduling> queryWrapper_delete = new QueryWrapper<>();
        queryWrapper_delete.isNull("delete_time").eq("id",sid);
        return vehicleSchedulingMapper.update(scheduling,queryWrapper_delete) == 1;
    }

    /**
     * @Author: liwencai
     * @Description: 修改调度信息 假定：开始时间不能改变
     * @Date: 2022/7/27
     * @Param paramVO:
     * @return: boolean
     */
    @Override
    @Transactional
    public Map<String, Object> updateSpec(VehicleSchedulingParamVO paramVO) {

        /* 查询这么一种调度：它的调度时间处于新增调度之间(除去自身) */
        QueryWrapper<TblVehicleScheduling> queryWrapper_1 = new QueryWrapper<>();
        queryWrapper_1.isNull("delete_time").eq("car_number",paramVO.getCarNumber())
                .ne("id",paramVO.getId())
                .ge("start_time",paramVO.getStartTime())
                .le("end_time",paramVO.getEndTime());
        List<TblVehicleScheduling> conflictSchedulingList_1 = vehicleSchedulingMapper.selectList(queryWrapper_1);
        if(conflictSchedulingList_1.size() > 0){
            log.warn("存在调度处于调度时间段之间，调度时间冲突");
            return getServiceResultMap("error","存在调度处于调度时间段之间，调度时间冲突",null);
        }

        /* 查询这么两种情况的调度(除去自身)：
         * 第一种；新增的调度的开始时间处于它的调度时间范围内；
         * 第二中调度：新增的调度的结束时间出它的调度时间内
         */
        QueryWrapper<TblVehicleScheduling> queryWrapper_2 = new QueryWrapper<>();
        queryWrapper_2.isNull("delete_time").eq("car_number",paramVO.getCarNumber())
                .ne("id",paramVO.getId())
                .and(e->e.and(x->x.lt("start_time",paramVO.getStartTime()).gt("end_time",paramVO.getStartTime()))
                        .or(x->x.lt("start_time",paramVO.getEndTime()).gt("end_time",paramVO.getEndTime())));
        List<TblVehicleScheduling> conflictSchedulingList_2 = vehicleSchedulingMapper.selectList(queryWrapper_2);
        if(conflictSchedulingList_2.size() > 0){
            log.warn("新增的调度信息的开始时间或结束时间处于别的调度中间");
            return getServiceResultMap("error","新增的调度信息的开始时间或结束时间处于别的调度中间",null);
        }

        /* 如果该调度的开始时间小于系统时间，结束时间大于系统时间，将公车状态修改为出车中 */
        LocalDateTime nowTime= LocalDateTime.now();
        if(paramVO.getStartTime().isBefore(nowTime) && paramVO.getEndTime().isAfter(nowTime)){
            Map<String,Object> map = new HashMap<>();
            map.put("vid", paramVO.getVehicleInfoId());
            map.put("status",1);
            map.put("updateBy",getOperatorName());
            vehicleInfoMapper.changeVehicleStatus(map);
            log.warn("{}，{}，系统时间在两者之间所以，修改该公车状态为正在调度",paramVO.getStartTime(),paramVO.getEndTime());
        }

        /* 修改信息 */
        TblVehicleScheduling scheduling = vehicleSchedulingConverter.toVehicleScheduling(paramVO);
        scheduling.setUpdateTime(LocalDateTime.now());
        scheduling.setUpdateBy(getOperatorName());
        QueryWrapper<TblVehicleScheduling> queryWrapper_update = new QueryWrapper<>();
        queryWrapper_update.isNull("delete_time").eq("id",paramVO.getId());
        // 计算改调度的秒数
        try {
            Long seconds = Math.abs(scheduling.getEndTime().until(scheduling.getStartTime(), ChronoUnit.SECONDS));
            scheduling.setWorkingDuration(seconds);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        if(vehicleSchedulingMapper.update(scheduling,queryWrapper_update) == 1){
            return getServiceResultMap("success",null,null);
        }else {
            return getServiceResultMap("error","新增调度信息失败",null);
        }
    }

    /**
     * @Author: liwencai
     * @Description: 条件查询调度信息
     * @Date: 2022/7/27
     * @Param paramVO:
     * @return: java.util.List<com.thtf.office.entity.TblVehicleScheduling>
     */
    @Override
    public List<TblVehicleScheduling> select(VehicleSchedulingParamVO paramVO) {
        return vehicleSchedulingMapper.select(paramVO);
    }

    /**
     * @Author: liwencai
     * @Description: 查询待命状态的司机的日、月出车情况
     * @Date: 2022/7/29
     * @return: java.util.List<com.thtf.office.vo.VehicleSelectByDateResult>
     */
    @Override
    public List<VehicleSelectByDateResult> selectInfoAboutDri() {
        List<VehicleSelectByDateResult> result = new ArrayList<>();
        // 获取每月司机出车情况
        List<VehicleSelectByDateResult> monthData = vehicleSchedulingMapper.selectScheAboutDir(getSelectScheAboutDirMap("monthNumber","%Y-%m"));
        // 获取每日司机出车情况
        List<VehicleSelectByDateResult> dayData = vehicleSchedulingMapper.selectScheAboutDir(getSelectScheAboutDirMap("dayNumber","%Y-%m-%d"));
        // 填补id在按月查时不为空，按日却为空时的数据
        for (VehicleSelectByDateResult vehicleSelectByDateResult : dayData) {
            int j = 0;
            while (!monthData.get(j).getId().equals(vehicleSelectByDateResult.getId())) {
                monthData.get(j).setDayNumber(0L);
                j = j + 1;
                if (monthData.get(j).getId().equals(vehicleSelectByDateResult.getId())) {
                    break;
                }
            }
            monthData.get(j).setDayNumber(vehicleSelectByDateResult.getDayNumber());
        }
        // 每日为数据为null时补0
        monthData.forEach(e->{
            if(e.getDayNumber() == null){
                e.setDayNumber(0L);
            }
        });
        // 所有司机信息
        JsonResult<List<TblUser>> dataJsonResult = adminAPI.searchUserByPosition("司机");
        List<TblUser> driverList = dataJsonResult.getData();
        // 填补所有在职司机日月出车信息
        for (TblUser o : driverList) {
            result.add(getVehicleSelectByDateResult(o.getId(),o.getName(),0L,0L));
            for (VehicleSelectByDateResult monthDatum : monthData) {
                if (o.getId().equals(monthDatum.getId())) {
                    result.removeIf(e-> e.getId().equals(o.getId()));
                    result.add(getVehicleSelectByDateResult(o.getId(), o.getName(), monthDatum.getMonthNumber(), monthDatum.getDayNumber()));
                    break;
                }
            }
        }
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 创建流水单号
     * @Date: 2022/8/28
     * @return: java.lang.String
     */
    @Override
    public String createSerialNumber() {
        ResponseEntity<JsonResult<List<TblBasicData>>> datas = adminAPI.searchBasicDataByType(30);
        List<TblBasicData> basicDatas = Objects.requireNonNull(datas.getBody()).getData();
        TblBasicData basicData = basicDatas.stream().filter(obj -> obj.getBasicName().contains("入库")).findFirst().get();
        String num = basicData.getBasicCode();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd");
        List<TblVehicleScheduling> infos = vehicleSchedulingMapper.selectList(new QueryWrapper<TblVehicleScheduling>().like("update_time", formatter.format(LocalDateTime.now(ZoneId.of("+8")))).orderByDesc("update_time"));
        if(!infos.isEmpty()){
            DecimalFormat dft = new DecimalFormat("000");
            num += formatter2.format(LocalDateTime.now(ZoneId.of("+8"))) +
                    dft.format(Integer.parseInt(infos.get(0).getCode().substring(infos.get(0).getCode().length()-3))+1);
        } else {
            num += formatter2.format(LocalDateTime.now(ZoneId.of("+8"))) + "001";
        }
        return num;
    }

    /**
     * @Author: liwencai
     * @Description: 创建VehicleSelectByDateResult实例
     * @Date: 2022/7/30
     * @Param id:
     * @Param attribute:
     * @Param monthNumber:
     * @Param dayNumber:
     * @return: com.thtf.office.vo.VehicleSelectByDateResult
     */
    VehicleSelectByDateResult getVehicleSelectByDateResult(Long id,String attribute,Long monthNumber,Long dayNumber){
        VehicleSelectByDateResult vehicleSelectByDateResult = new VehicleSelectByDateResult();
        vehicleSelectByDateResult.setId(id);
        vehicleSelectByDateResult.setAttribute(attribute);
        vehicleSelectByDateResult.setMonthNumber(monthNumber);
        vehicleSelectByDateResult.setDayNumber(dayNumber);
        return vehicleSelectByDateResult;
    }

    /**
     * @Author: liwencai
     * @Description: 查询待命状态的司机的日、月出车情况的传参Map
     * @Date: 2022/7/29
     * @Param numberType: 每月：monthNumber 每日：dayNumber
     * @Param dateTemplate: 每月："%Y-%m" 每日："%Y-%m-%d"
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String,Object> getSelectScheAboutDirMap(String numberType,String dateTemplate) {
        Map<String, Object> map = new HashMap<>();
        map.put("numberType", numberType);
        map.put("dateTemplate", dateTemplate);
        return map;
    }

    /**
     * @Author: liwencai
     * @Description: 获取操作人姓名
     * @Date: 2022/8/2
     * @return: null
     */
    public String getOperatorName(){
        UserInfo userInfo = null;
        String realName = null;
        try {
            userInfo = adminAPI.userInfo(HttpUtil.getToken());
        }catch (Exception e){
            log.info("远程调用根据token查询用户信息失败失败");
        }
        if(null !=  userInfo){
            realName = userInfo.getRealname();
        }
        /*String userName = SecurityContextHolder.getUserName();
        System.out.println("XXXXXXX"+userName);*/
        return realName;
    }

    /**
     * @Author: liwencai
     * @Description: service层结果集封装，需要在service层返回controller层详细信息时使用
     * @Date: 2022/7/31
     * @Param status: 状态（“success”,"error"）
     * @Param errorCause: 错误原因
     * @Param result: 正确结果
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String,Object> getServiceResultMap(String status,String errorCause,Object result){
        Map<String,Object> map = new HashMap<>();
        map.put("status",status);
        map.put("errorCause",errorCause);
        map.put("result",result);
        return map;
    }
}
