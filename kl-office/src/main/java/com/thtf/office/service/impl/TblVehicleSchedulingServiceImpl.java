package com.thtf.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thtf.office.common.response.JsonResult;
import com.thtf.office.dto.TblUser;
import com.thtf.office.dto.TblUserScheduleDTO;
import com.thtf.office.feign.AdminAPI;
import com.thtf.office.vo.VehicleSchedulingParamVO;
import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.mapper.TblVehicleSchedulingMapper;
import com.thtf.office.service.TblVehicleSchedulingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

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

    @Autowired
    private AdminAPI adminAPI;

    @Override
    public boolean deleteById(Long sid) {
        TblVehicleScheduling scheduling = vehicleSchedulingMapper.selectById(sid);
        if(null != scheduling){
            scheduling.setDeleteTime(LocalDateTime.now());
            //todo scheduling.setDeleteBy();
            QueryWrapper<TblVehicleScheduling> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNull("delete_time").eq("id",sid);
            return vehicleSchedulingMapper.update(scheduling,queryWrapper) == 1;
        }
        return false;
    }

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
