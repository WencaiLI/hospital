package com.thtf.office.dto;

import com.thtf.office.entity.TblVehicleCategory;
import com.thtf.office.vo.VehicleCategoryParamVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Auther: liwencai
 * @Date: 2022/7/26 22:57
 * @Description: bean映射
 */
@Mapper(componentModel = "spring")
public interface VehicleCategoryConvert {

    VehicleCategoryConvert INSTANCE = Mappers.getMapper(VehicleCategoryConvert.class);
    TblVehicleCategory toVehicleCategory(VehicleCategoryParamVO vehicleCategoryParamVO);
}
