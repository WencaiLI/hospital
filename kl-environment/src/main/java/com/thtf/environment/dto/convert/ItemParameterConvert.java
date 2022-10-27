package com.thtf.environment.dto.convert;

import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.environment.vo.ItemParameterInfoVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/27 15:11
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface ItemParameterConvert {

    List<ItemParameterInfoVO> toItemParameterInfoVO(List<TblItemParameter> itemParameterInfoVOList);
}
