package com.thtf.elevator.dto.convert;

import com.thtf.common.entity.adminserver.TblBuildingArea;
import com.thtf.elevator.dto.FloorInfoDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/8 09:18
 * @Description: 楼层bean转换接口
 */
@Mapper(componentModel = "spring")
public interface FloorConverter {
    List<FloorInfoDTO> toFloorList(List<TblBuildingArea> tblBuildingAreaList);
}
