package com.thtf.elevator.dto.convert;

import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.elevator.dto.ParameterInfoDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/8 10:01
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface ParameterConverter {

    ParameterInfoDTO toParameterInfo(TblItemParameter tblItemParameterList);
    List<ParameterInfoDTO> toParameterInfoList(List<TblItemParameter> tblItemParameterList);
}
