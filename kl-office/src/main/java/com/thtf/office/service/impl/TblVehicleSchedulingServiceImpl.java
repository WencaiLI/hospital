package com.thtf.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thtf.office.common.response.JsonResult;
import com.thtf.office.common.util.IdGeneratorSnowflake;
import com.thtf.office.dto.VehicleSchedulingConvert;
import com.thtf.office.mapper.TblVehicleInfoMapper;
import com.thtf.office.dto.TblUser;
import com.thtf.office.dto.TblUserScheduleDTO;
import com.thtf.office.feign.AdminAPI;
import com.thtf.office.vo.VehicleSchedulingParamVO;
import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.mapper.TblVehicleSchedulingMapper;
import com.thtf.office.service.TblVehicleSchedulingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    VehicleSchedulingConvert vehicleSchedulingConvert;

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
        TblVehicleScheduling scheduling = vehicleSchedulingConvert.toVehicleScheduling(paramVO);
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
        /* 新增业务逻辑 假定：开始时间不能改变 */
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
        TblVehicleScheduling scheduling = vehicleSchedulingConvert.toVehicleScheduling(paramVO);
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

    @Override
    public List<TblUserScheduleDTO> findDriverForSchedule(String positionTitle) {
        //获取外部接口人员数据
        JsonResult<List<TblUser>> dataJsonResult = adminAPI.searchUserByPosition(positionTitle);
        List<TblUser> data = dataJsonResult.getData();
        //组装人员信息及出车次数信息
        return null;
    }
}
