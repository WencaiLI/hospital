package com.thtf.face_recognition.dto.mapstruct;



import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.face_recognition.vo.TblItemParameterVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @Author: liwencai
 * @Description:
 * @Date: 2022/11/14
 */
@Mapper(componentModel = "spring")
@SuppressWarnings("all")
public interface FaceRecognitionItemConvert {

    TblItemParameterVO toTblItemParameterVO(TblItemParameter tblItemParameter);

    List<TblItemParameterVO> toTblItemParameterVOList(List<TblItemParameter> tblItemParameterList);
}