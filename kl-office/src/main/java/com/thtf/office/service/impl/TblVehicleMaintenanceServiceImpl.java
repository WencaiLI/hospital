package com.thtf.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thtf.office.vo.VehicleMaintenanceParamVO;
import com.thtf.office.entity.TblVehicleMaintenance;
import com.thtf.office.mapper.TblVehicleMaintenanceMapper;
import com.thtf.office.service.TblVehicleMaintenanceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 车辆维保表 服务实现类
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
@Service
public class TblVehicleMaintenanceServiceImpl extends ServiceImpl<TblVehicleMaintenanceMapper, TblVehicleMaintenance> implements TblVehicleMaintenanceService {

    @Resource
    TblVehicleMaintenanceMapper vehicleMaintenanceMapper;

    /**
     * @Author: liwencai
     * @Description: 根据维保id删除维保信息
     * @Date: 2022/7/26
     * @Param mid:
     * @return: boolean
     */
    @Override
    public boolean deleteById(Long mid) {
        TblVehicleMaintenance maintenance = vehicleMaintenanceMapper.selectById(mid);
        if(null != maintenance){
            maintenance.setDeleteTime(LocalDateTime.now());
            // todo maintenance.setDeleteBy();
            QueryWrapper<TblVehicleMaintenance> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNull("delete_time").eq("id",mid);
            return vehicleMaintenanceMapper.update(maintenance,queryWrapper) == 1;
        }
        return false;
    }

    /**
     * @Author: liwencai
     * @Description: 条件查询
     * @Date: 2022/7/28
     * @Param vehicleMaintenanceParamVO:
     * @return: java.util.List<com.thtf.office.entity.TblVehicleMaintenance>
     */
    @Override
    public List<TblVehicleMaintenance> select(VehicleMaintenanceParamVO vehicleMaintenanceParamVO) {
        return vehicleMaintenanceMapper.select(vehicleMaintenanceParamVO);
    }
}
