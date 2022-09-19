package com.thtf.elevator.dto.convert;

import com.thtf.common.dto.itemserver.TblItemDTO;
import com.thtf.common.entity.itemserver.TblItem;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/5 21:47
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface ItemConverter {
    List<TblItem> toItemList(List<TblItemDTO> tblItemDTOS);
}
