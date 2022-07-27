package com.thtf.office.dto;

import com.thtf.office.entity.TblVehicleMaintenance;
import com.thtf.office.vo.VehicleMaintenanceParamVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Auther: liwencai
 * @Date: 2022/7/26 23:06
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface VehicleMaintenanceConvert {

    VehicleMaintenanceConvert INSTANCE = Mappers.getMapper(VehicleMaintenanceConvert.class);
    TblVehicleMaintenance toVehicleMaintenance(VehicleMaintenanceParamVO vehicleMaintenanceParamVO);
}
