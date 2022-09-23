package com.thtf.environment.dto.convert;

import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.environment.dto.ItemInfoOfLargeScreenDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


import java.util.List;

/**
 * @Auther: liwencai
 * @Date: 2022/9/23 11:09
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface ItemConvert {



    @Mappings({
            @Mapping(source = "id",target = "itemId"),
            @Mapping(source = "name",target = "itemName"),
            @Mapping(source = "code",target = "itemCode"),
            @Mapping(source = "areaCode", target = "areaCode")
    })
    ItemInfoOfLargeScreenDTO toItemInfoOfLS (TblItem tblItemList);

    /**
     * @Author: liwencai
     * @Description: 将 tblItemList 转换为 ItemInfoOfLargeScreenDTO
     * @Date: 2022/9/23
     * @Param tblItemList:
     * @return: java.util.List<com.thtf.environment.dto.ItemInfoOfLargeScreenDTO>
     */

    List<ItemInfoOfLargeScreenDTO> toItemInfoOfLSList(List<TblItem> tblItemList);
}
