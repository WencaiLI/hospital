package com.thtf.environment.dto.convert;

import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.environment.dto.ItemInfoOfLargeScreenDTO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liwencai
 * @Date: 2022/9/23 11:09
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface ItemConvert {



    /**
     * @Author: liwencai
     * @Description: 将 tblItem 转换为 ItemInfoOfLargeScreenDTO
     * @Date: 2022/10/7
     * @Param tblItemList:
     * @return: com.thtf.environment.dto.ItemInfoOfLargeScreenDTO
     */
    @Mappings({
            @Mapping(source = "id",target = "itemId"),
            @Mapping(source = "name",target = "itemName"),
            @Mapping(source = "code",target = "itemCode"),
            @Mapping(source = "areaCode", target = "areaCode"),
            @Mapping(source = "viewLongitude", target = "eye"),
            @Mapping(source = "viewLatitude", target = "center")

    })
    ItemInfoOfLargeScreenDTO toItemInfoOfLS (TblItem tblItemList);

    /* 当string 转 list时执行*/
    default List<Integer> str2List(String source){
        if(StringUtils.isNotBlank(source)){
            String[] split = source.split(",");
            List<String> result = Arrays.asList(split);
            return  result.stream().map(Integer::valueOf).collect(Collectors.toList());
        }else {
            return null;
        }
    }
    /**
     * @Author: liwencai
     * @Description: 将 tblItemList 转换为 ItemInfoOfLargeScreenDTO List
     * @Date: 2022/9/23
     * @Param tblItemList:
     * @return: java.util.List<com.thtf.environment.dto.ItemInfoOfLargeScreenDTO>
     */
    List<ItemInfoOfLargeScreenDTO> toItemInfoOfLSList(List<TblItem> tblItemList);
}
