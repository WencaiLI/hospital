package com.thtf.office.dto.converter;

import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.vo.VehicleSchedulingParamVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Auther: liwencai
 * @Date: 2022/7/26 23:09
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface VehicleSchedulingConverter {
    TblVehicleScheduling toVehicleScheduling(VehicleSchedulingParamVO vehicleSchedulingParamVO);
}
