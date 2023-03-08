package com.thtf.elevator.dto.convert;

import com.thtf.common.dto.itemserver.ItemNestedParameterVO;
import com.thtf.common.dto.itemserver.ListItemNestedParametersResultDTO;
import com.thtf.common.dto.itemserver.TblItemDTO;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.elevator.dto.ElevatorAlarmResultDTO;
import com.thtf.elevator.dto.ElevatorInfoResultDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/5 21:47
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface ItemConverter {
    List<TblItem> toItemList(List<TblItemDTO> tblItemDTOS);

    @Mappings({
            @Mapping(source = "id",target = "itemId"),
            @Mapping(source = "code",target = "itemCode"),
            @Mapping(source = "name",target = "itemName"),
            @Mapping(source = "areaName",target = "areaName"),
    })
    ElevatorInfoResultDTO toElevatorInfo(ItemNestedParameterVO itemNestedParameterVO);

    List<ElevatorInfoResultDTO> toElevatorInfoList(List<ItemNestedParameterVO> itemNestedParameterVOList);

    @Mappings({
            @Mapping(source = "buildingAreaCode",target = "areaCode"),
            @Mapping(source = "buildingAreaName",target = "areaName"),
    })
    ElevatorInfoResultDTO toElevatorInfo(ListItemNestedParametersResultDTO itemNestedParameterVO);

    List<ElevatorInfoResultDTO> toElevatorInfoResultList(List<ListItemNestedParametersResultDTO> itemNestedParameterVOList);
//    @Mappings({
//            @Mapping(source = "id",target = "itemId"),
//            @Mapping(source = "code",target = "itemCode"),
//            @Mapping(source = "name",target = "itemName"),
//            @Mapping(source = "areaName",target = "areaName"),
//    })
//    ElevatorAlarmResultDTO toElevatorAlarmInfo(ItemNestedParameterVO itemNestedParameterVO);

//    List<ElevatorAlarmResultDTO> toElevatorAlarmInfoList(List<ItemNestedParameterVO> itemNestedParameterVO);


}
