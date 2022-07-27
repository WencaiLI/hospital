package com.thtf.office.dto;

import com.thtf.office.entity.TblVehicleInfo;
import com.thtf.office.vo.VehicleInfoParamVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Auther: liwencai
 * @Date: 2022/7/26 23:04
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface VehicleInfoConvert {

    VehicleInfoConvert INSTANCE = Mappers.getMapper(VehicleInfoConvert.class);
    TblVehicleInfo toVehicleInfo(VehicleInfoParamVO vehicleInfoParamVO);
}
