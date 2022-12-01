package com.thtf.environment.dto.convert;

import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.environment.dto.ParameterInfoDTO;
import com.thtf.environment.vo.ItemParameterInfoVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/27 15:11
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface ParameterConverter {
    ParameterInfoDTO toParameterInfo(TblItemParameter tblItemParameterList);

    List<ParameterInfoDTO> toParameterInfoList(List<TblItemParameter> tblItemParameterList);

    List<ItemParameterInfoVO> toItemParameterInfoVOList(List<TblItemParameter> tblItemParameterList);

}
