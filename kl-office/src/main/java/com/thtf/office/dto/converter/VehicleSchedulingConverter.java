package com.thtf.office.dto.converter;

import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.vo.VehicleSchedulingParamVO;
import com.thtf.office.vo.VehicleSchedulingQueryVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/7/26 23:09
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface VehicleSchedulingConverter {
    TblVehicleScheduling toVehicleScheduling(VehicleSchedulingParamVO vehicleSchedulingParamVO);

    VehicleSchedulingQueryVO toVehicleSchedulingQueryVO(TblVehicleScheduling vehicleScheduling);

    List<VehicleSchedulingQueryVO> toVehicleSchedulingQueryVOList(List<TblVehicleScheduling> vehicleSchedulingList);
}
