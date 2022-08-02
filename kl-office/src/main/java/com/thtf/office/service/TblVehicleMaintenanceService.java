package com.thtf.office.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.office.entity.TblVehicleMaintenance;
import com.thtf.office.vo.VehicleMaintenanceParamVO;

import java.util.List;

/**
 * <p>
 * 车辆维保表 服务类
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
public interface TblVehicleMaintenanceService extends IService<TblVehicleMaintenance> {

    boolean deleteById(Long mid);

    List<TblVehicleMaintenance> select(VehicleMaintenanceParamVO vehicleMaintenanceParamVO);

    Boolean insert(VehicleMaintenanceParamVO vehicleMaintenanceParamVO);

    boolean updateSpec(VehicleMaintenanceParamVO vehicleMaintenanceParamVO);
}
