package com.thtf.office.dto.converter;

import com.thtf.office.entity.TblVehicleMaintenance;
import com.thtf.office.vo.VehicleMaintenanceParamVO;
import org.mapstruct.Mapper;

/**
 * @Author: liwencai
 * @Date: 2022/7/26 23:06
 * @Description: 公车维保信息bean映射转换器
 */
@Mapper(componentModel = "spring")
public interface VehicleMaintenanceConverter {
    TblVehicleMaintenance toVehicleMaintenance(VehicleMaintenanceParamVO vehicleMaintenanceParamVO);
}
