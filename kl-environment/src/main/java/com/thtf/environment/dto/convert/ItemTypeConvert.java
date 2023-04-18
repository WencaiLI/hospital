package com.thtf.environment.dto.convert;

import com.thtf.common.dto.itemserver.CodeAndNameDTO;
import com.thtf.common.entity.itemserver.TblItemType;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/26 20:31
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface ItemTypeConvert {

    /**
     * @Author: liwencai
     * @Description: List<TblItemType> 转换为 List<CodeNameVO>
     * @Date: 2022/10/26
     * @Param itemTypeList: 设备类别集
     * @return: java.util.List<com.thtf.environment.vo.CodeNameVO>
     */
    List<CodeAndNameDTO> toCodeNameVO(List<TblItemType> itemTypeList);
}
