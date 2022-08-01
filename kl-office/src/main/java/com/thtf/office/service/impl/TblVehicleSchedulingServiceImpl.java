package com.thtf.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thtf.office.common.response.JsonResult;
import com.thtf.office.common.util.IdGeneratorSnowflake;
import com.thtf.office.dto.converter.VehicleSchedulingConverter;
import com.thtf.office.mapper.TblVehicleInfoMapper;
import com.thtf.office.common.entity.adminserver.TblBasicData;
import com.thtf.office.common.entity.adminserver.TblUser;
import com.thtf.office.feign.AdminAPI;
import com.thtf.office.vo.VehicleSchedulingParamVO;
import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.mapper.TblVehicleSchedulingMapper;
import com.thtf.office.service.TblVehicleSchedulingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.office.vo.VehicleSelectByDateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
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
public class TblVehicleSchedulingServiceImpl extends ServiceImpl<TblVehicleSchedulingMapper, TblVehicleScheduling> implements TblVehicleSchedulingService {

    @Resource
    TblVehicleSchedulingMapper vehicleSchedulingMapper;

    @Resource
    TblVehicleInfoMapper vehicleInfoMapper;

    @Resource
    VehicleSchedulingConverter vehicleSchedulingConverter;

    @Autowired
    private IdGeneratorSnowflake idGeneratorSnowflake;

    /**
     * @Author: liwencai
     * @Description: 新增调度信息
     * @Date: 2022/7/27
     * @Param paramVO:
     * @return: boolean
     */
    @Override
    @Transactional
    public boolean insert(VehicleSchedulingParamVO paramVO) {
        /* 查询调度记录并看是否有违规调度（在同一辆车的调度时间上存在冲突） */
        QueryWrapper<TblVehicleScheduling> queryWrapper = new QueryWrapper<>();
        // todo 假如接入Redis做数据统计时以下代码有优化空间
        // 取最近一次的调度记录
        queryWrapper.isNull("delete_time").eq("car_number",paramVO.getCarNumber()).last("ORDER BY start_time DESC LIMIT 1");
        List<TblVehicleScheduling> schedulings = vehicleSchedulingMapper.selectList(queryWrapper);
        // 此前没有调度记录，直接添加
        TblVehicleScheduling scheduling = vehicleSchedulingConverter.toVehicleScheduling(paramVO);
        scheduling.setId(this.idGeneratorSnowflake.snowflakeId());
        scheduling.setCreateTime(LocalDateTime.now());
        // todo scheduling.setCreateBy();
        if (schedulings.size() == 0){
            return vehicleSchedulingMapper.insert(scheduling) == 1;
        }
        // 此前有调度记录，比较新调度起始时间与最近调度的结束时间比较是否冲突，不冲突则新增
        // todo 可以返回Map记录失败具体原因
        LocalDateTime endTime = paramVO.getEndTime();
        if(Duration.between(endTime, schedulings.get(0).getStartTime()).toMillis() > 0){
            return vehicleSchedulingMapper.insert(scheduling) == 1;
        }
        return false;
    }

    /**
     * @Author: liwencai
     * @Description: 根据id删除调度信息
     * @Date: 2022/7/27
     * @Param sid:
     * @return: boolean
     */
    @Autowired
    private AdminAPI adminAPI;

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
            map.put("updateBy",null);
            return vehicleInfoMapper.changeVehicleStatus(map) == 1;
        }
        // 2 调度已经结束时
        scheduling.setDeleteTime(LocalDateTime.now());
        //todo scheduling.setDeleteBy();
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
    public boolean updateSpec(VehicleSchedulingParamVO paramVO) {
        /* 修改业务逻辑 假定：开始时间不能改变 */
        // 1、查询是否有这么一个调度：它的调度结束时间在新调度的开始时间与结束时间之间
        QueryWrapper<TblVehicleScheduling> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").between("end_time",paramVO.getStartTime(),paramVO.getEndTime());
        List<TblVehicleScheduling> schedulings = vehicleSchedulingMapper.selectList(queryWrapper);
        // 1.1 假如有多条调度记录，返回false
        if(schedulings.size() > 1){
            return false;
        }
        // 1.2 假如有一条调度记录，且不是原调度，则调度冲突，返回false
        if (schedulings.size() == 1 && !schedulings.get(0).getId().equals(paramVO.getId())){
            return false;
        }
        // 1.3 剩余情况：没有这样的记录，不会冲突 或 有一条调度记录，但不是原调度，不会冲突，直接修改
        TblVehicleScheduling scheduling = vehicleSchedulingConverter.toVehicleScheduling(paramVO);
        scheduling.setUpdateTime(LocalDateTime.now());
        // todo tblVehicleScheduling.setUpdateBy
        QueryWrapper<TblVehicleScheduling> queryWrapper_update = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").eq("id",paramVO.getId());
        return vehicleSchedulingMapper.update(scheduling,queryWrapper_update) == 1;
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

//    /**
//     * @Author: liwencai
//     * @Description: 查询司机
//     * @Date: 2022/7/29
//     * @Param positionTitle:
//     * @return: java.util.List<com.thtf.office.dto.TblUserScheduleDTO>
//     */
//    @Override
//    public List<TblUser> findDriverForSchedule(String positionTitle) {
//        //获取外部接口人员数据
//        JsonResult<List<TblUser>> dataJsonResult = adminAPI.searchUserByPosition(positionTitle);
//        List<TblUser> data = dataJsonResult.getData();
//        //组装人员信息及出车次数信息
//        return data;
//    }

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
        // 所有司机信息
        JsonResult<List<TblUser>> dataJsonResult = adminAPI.searchUserByPosition("司机");
        List<TblUser> driverList = dataJsonResult.getData();
        // 填补所有在职司机日月出车信息
        for (TblUser o : driverList) {
            result.add(getVehicleSelectByDateResult(o.getId(),o.getName(),0L,0L));
            for (VehicleSelectByDateResult monthDatum : monthData) {
                if (o.getId().equals(monthDatum.getId())) {
                    result.removeIf(e->e.getId()==o.getId());
                    result.add(getVehicleSelectByDateResult(o.getId(), o.getName(), monthDatum.getMonthNumber(), monthDatum.getDayNumber()));
                    break;
                }
            }
        }
        return result;
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

    // todo 此处可创建构造函数替代，但尚未验证构造函数的影响，待验证不影响其他功能后使用构造函数替换
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
}
