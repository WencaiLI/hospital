package com.thtf.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thtf.common.entity.adminserver.TblBasicData;
import com.thtf.common.entity.adminserver.TblUser;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.common.security.SecurityContextHolder;
import com.thtf.common.util.IdGeneratorSnowflake;
import com.thtf.office.common.enums.VehicleSchedulingStatusEnum;
import com.thtf.office.common.enums.VehicleStatusEnum;
import com.thtf.office.common.util.CodeGeneratorUtil;
import com.thtf.office.dto.converter.VehicleSchedulingConverter;
import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.mapper.TblVehicleInfoMapper;
import com.thtf.office.mapper.TblVehicleSchedulingMapper;
import com.thtf.office.service.TblVehicleSchedulingService;
import com.thtf.office.vo.VehicleSchedulingParamVO;
import com.thtf.office.vo.VehicleSchedulingQueryVO;
import com.thtf.office.vo.VehicleSelectByDateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
        QueryWrapper<TblVehicleScheduling> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().isNull(TblVehicleScheduling::getDeleteTime)
                .eq(TblVehicleScheduling::getCarNumber,paramVO.getCarNumber())
                .ge(TblVehicleScheduling::getStartTime,paramVO.getStartTime())
                .le(TblVehicleScheduling::getEndTime,paramVO.getEndTime());
        long l = vehicleSchedulingMapper.selectCount(queryWrapper1).longValue();
        if(l > 0){
            return getServiceResultMap("error","存在调度处于调度时间段之间，调度时间冲突",null);
        }

        /* 查询这么两种情况的调度：
         * 第一种；新增的调度的开始时间处于它的调度时间范围内；
         * 第二中调度：新增的调度的结束时间处于它的调度时间内
         */
        QueryWrapper<TblVehicleScheduling> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.lambda().isNull(TblVehicleScheduling::getDeleteTime)
                .eq(TblVehicleScheduling::getCarNumber,paramVO.getCarNumber())
                .and(x->x.and(e->e.lt(TblVehicleScheduling::getStartTime,paramVO.getStartTime())
                        .gt(TblVehicleScheduling::getEndTime,paramVO.getStartTime())
                        )
                        .or(e->e.lt(TblVehicleScheduling::getStartTime,paramVO.getEndTime())
                                .gt(TblVehicleScheduling::getEndTime,paramVO.getEndTime())
                        )
                );
        long l1 = vehicleSchedulingMapper.selectCount(queryWrapper2).longValue();
        if(l1 > 0){
            return getServiceResultMap("error","新增的调度信息的开始时间或结束时间处于别的调度中间",null);
        }

        /* Bean映射 */
        TblVehicleScheduling scheduling = vehicleSchedulingConverter.toVehicleScheduling(paramVO);
        scheduling.setId(this.idGeneratorSnowflake.snowflakeId());
        scheduling.setCreateTime(LocalDateTime.now());
        scheduling.setCreateBy( SecurityContextHolder.getUserName());

        /* 如果该调度的开始时间小于系统时间，结束时间大于系统时间，将公车状态修改为出车中 */
        LocalDateTime nowTime= LocalDateTime.now();
        if(paramVO.getStartTime().isBefore(nowTime) && paramVO.getEndTime().isAfter(nowTime)){
            Map<String,Object> map = new HashMap<>();
            map.put("vid", paramVO.getVehicleInfoId());
            map.put("status", VehicleStatusEnum.OUT.getStatus());
            map.put("updateBy", SecurityContextHolder.getUserName());
            vehicleInfoMapper.changeVehicleStatus(map);
            scheduling.setStatus(VehicleSchedulingStatusEnum.IN_SCHEDULING.getStatus());
        }
        /* 调度已经结束的调度 */
        if(paramVO.getStartTime().isBefore(nowTime) && paramVO.getEndTime().isBefore(nowTime)){
            scheduling.setStatus(VehicleSchedulingStatusEnum.END_OF_SCHEDULING.getStatus());
        }
        /* 尚未开始的调度 修改为待命中状态*/
        if(paramVO.getStartTime().isAfter(nowTime) && paramVO.getEndTime().isAfter(nowTime)){
            scheduling.setStatus(VehicleSchedulingStatusEnum.NOT_START_SCHEDULING.getStatus());
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
        TblVehicleScheduling scheduling = vehicleSchedulingMapper.selectById(sid);
        if(null == scheduling){
            return false;
        }
        // 1. 正在调度的调度
        if (VehicleSchedulingStatusEnum.IN_SCHEDULING.getStatus().equals(scheduling.getStatus())){
            vehicleInfoMapper.changeVehicleStatus(getUpdateInfoStatusMap(scheduling.getVehicleInfoId(),VehicleStatusEnum.STANDBY.getStatus(),
                    SecurityContextHolder.getUserName(),null));
        }
        // 2. 已经结束的调度
        if (VehicleSchedulingStatusEnum.END_OF_SCHEDULING.getStatus().equals(scheduling.getStatus())){
            vehicleInfoMapper.changeVehicleStatus(getUpdateInfoStatusMap(scheduling.getVehicleInfoId(),null,
                    SecurityContextHolder.getUserName(),-(scheduling.getWorkingDuration())));
        }
        scheduling.setDeleteTime(LocalDateTime.now());
        scheduling.setDeleteBy(SecurityContextHolder.getUserName());
        QueryWrapper<TblVehicleScheduling> queryWrapperDelete = new QueryWrapper<>();
        queryWrapperDelete.lambda().isNull(TblVehicleScheduling::getDeleteTime).eq(TblVehicleScheduling::getId,sid);
        return vehicleSchedulingMapper.update(scheduling,queryWrapperDelete) == 1;
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
        // 查询原调度
        TblVehicleScheduling originalScheduling = vehicleSchedulingMapper.selectById(paramVO.getId());

        /* 查询这么一种调度：它的调度时间处于新增调度之间 */
        QueryWrapper<TblVehicleScheduling> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().isNull(TblVehicleScheduling::getDeleteTime)
                .eq(TblVehicleScheduling::getCarNumber,paramVO.getCarNumber())
                .ge(TblVehicleScheduling::getStartTime,paramVO.getStartTime())
                .le(TblVehicleScheduling::getEndTime,paramVO.getEndTime());
        TblVehicleScheduling tblVehicleScheduling = vehicleSchedulingMapper.selectOne(queryWrapper1);
        if(null != tblVehicleScheduling){
            if(!tblVehicleScheduling.getId().equals(paramVO.getId())){
                return getServiceResultMap("error","存在调度处于调度时间段之间，调度时间冲突",null);
            }
        }


        /* 查询这么两种情况的调度：
         * 第一种；新增的调度的开始时间处于它的调度时间范围内；
         * 第二中调度：新增的调度的结束时间处于它的调度时间内
         */
        QueryWrapper<TblVehicleScheduling> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.lambda().isNull(TblVehicleScheduling::getDeleteTime)
                .eq(TblVehicleScheduling::getCarNumber,paramVO.getCarNumber())
                .and(x->x.and(e->e.lt(TblVehicleScheduling::getStartTime,paramVO.getStartTime())
                                .gt(TblVehicleScheduling::getEndTime,paramVO.getStartTime())
                        )
                                .or(e->e.lt(TblVehicleScheduling::getStartTime,paramVO.getEndTime())
                                        .gt(TblVehicleScheduling::getEndTime,paramVO.getEndTime())
                                )
                );
        TblVehicleScheduling tblVehicleScheduling1 = vehicleSchedulingMapper.selectOne(queryWrapper2);
        if(null != tblVehicleScheduling1){
            if(!tblVehicleScheduling1.getId().equals(paramVO.getId())){
                return getServiceResultMap("error","新增的调度信息的开始时间或结束时间处于别的调度中间",null);
            }
        }
        /* Bean映射 */
        TblVehicleScheduling scheduling = vehicleSchedulingConverter.toVehicleScheduling(paramVO);
        scheduling.setId(this.idGeneratorSnowflake.snowflakeId());
        scheduling.setCreateTime(LocalDateTime.now());
        scheduling.setCreateBy( SecurityContextHolder.getUserName());

        Long seconds = Math.abs(scheduling.getEndTime().until(scheduling.getStartTime(), ChronoUnit.SECONDS));
        scheduling.setWorkingDuration(seconds);

        long workingDuration = 0;
        /* 如果该调度的开始时间小于系统时间，结束时间大于系统时间，将公车状态修改为出车中 */
        Integer newVehicleStatus = null;
        LocalDateTime nowTime= LocalDateTime.now();
        if(paramVO.getStartTime().isBefore(nowTime) && paramVO.getEndTime().isAfter(nowTime)){
            newVehicleStatus = VehicleStatusEnum.OUT.getStatus();
            scheduling.setStatus(VehicleSchedulingStatusEnum.IN_SCHEDULING.getStatus());
            if(VehicleSchedulingStatusEnum.END_OF_SCHEDULING.getStatus().equals(originalScheduling.getStatus())){
                workingDuration = -(originalScheduling.getWorkingDuration());
            }
        }

        /* 调度已经结束的调度 */
        if(paramVO.getStartTime().isBefore(nowTime) && paramVO.getEndTime().isBefore(nowTime)){
            newVehicleStatus = VehicleStatusEnum.STANDBY.getStatus();
            scheduling.setStatus(VehicleSchedulingStatusEnum.END_OF_SCHEDULING.getStatus());
            workingDuration = seconds - originalScheduling.getWorkingDuration();
        }
        /* 尚未开始的调度 修改为待命中状态*/
        if(paramVO.getStartTime().isAfter(nowTime) && paramVO.getEndTime().isAfter(nowTime)){
            scheduling.setStatus(VehicleSchedulingStatusEnum.NOT_START_SCHEDULING.getStatus());
            if(VehicleSchedulingStatusEnum.END_OF_SCHEDULING.getStatus().equals(originalScheduling.getStatus())){
                workingDuration = -(originalScheduling.getWorkingDuration());
            }
        }
        QueryWrapper<TblVehicleScheduling> queryWrapperUpdate = new QueryWrapper<>();
        queryWrapperUpdate.lambda().isNull(TblVehicleScheduling::getDeleteTime).eq(TblVehicleScheduling::getId,paramVO.getId());
        // 计算改调度的总秒数
        vehicleInfoMapper.changeVehicleStatus(getUpdateInfoStatusMap(paramVO.getVehicleInfoId(),newVehicleStatus,SecurityContextHolder.getUserName(),workingDuration));

        if(vehicleSchedulingMapper.update(scheduling,queryWrapperUpdate) == 1){
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
    public List<VehicleSchedulingQueryVO> select(VehicleSchedulingParamVO paramVO) {
        if(StringUtils.isNotBlank(paramVO.getKeywords())){
            paramVO.setKeyCarNumber(paramVO.getKeywords());
            paramVO.setKeyDestination(paramVO.getKeywords());
        }
        List<TblVehicleScheduling> select = vehicleSchedulingMapper.select(paramVO);
        return vehicleSchedulingConverter.toVehicleSchedulingQueryVOList(select);
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
        return CodeGeneratorUtil.getCode(num);
    }

    @Override
    public PageInfo<VehicleSchedulingQueryVO> selectPage(VehicleSchedulingParamVO paramVO) {
        PageInfo<VehicleSchedulingQueryVO> result = new PageInfo<>();
        PageHelper.startPage(paramVO.getPageNumber(),paramVO.getPageSize());
        PageInfo<TblVehicleScheduling> of = PageInfo.of(vehicleSchedulingMapper.select(paramVO));
        BeanUtils.copyProperties(of,result);
        result.setList(vehicleSchedulingConverter.toVehicleSchedulingQueryVOList(of.getList()));
        return result;
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

    /**
     * @Author: liwencai
     * @Description: 获取修改公车状态的参数map
     * @Date: 2022/7/29
     * @Param vehicleId:
     * @Param newStatus:
     * @Param updateBy:
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String,Object> getUpdateInfoStatusMap(Long vehicleId,Integer newStatus,String updateBy,Long workingDuration){
        Map<String,Object> map = new HashMap<>();
        map.put("vid", vehicleId);
        map.put("status",newStatus);
        map.put("updateBy",updateBy);
        map.put("workingDuration",workingDuration);
        return map;
    }
}
