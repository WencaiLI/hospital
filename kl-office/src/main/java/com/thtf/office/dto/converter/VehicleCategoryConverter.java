package com.thtf.office.dto.converter;

import com.thtf.office.entity.TblVehicleCategory;
import com.thtf.office.vo.VehicleCategoryParamVO;
import com.thtf.office.vo.VehicleCategoryResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/7/26 22:57
 * @Description: 公车类别bean映射转换器
 */
@Mapper(componentModel = "spring")
public interface VehicleCategoryConverter {
    TblVehicleCategory toVehicleCategory(VehicleCategoryParamVO vehicleCategoryParamVO);
    List<VehicleCategoryResultVO> categoryListToOtherList(List<TblVehicleCategory> tblVehicleCategoryList);
}
