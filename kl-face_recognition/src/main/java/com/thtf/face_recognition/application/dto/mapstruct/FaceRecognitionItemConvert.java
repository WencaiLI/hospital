package com.thtf.face_recognition.application.dto.mapstruct;



import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.face_recognition.adapter.driven.persistence.model.MegviiAlarmData;
import com.thtf.face_recognition.application.vo.TblItemParameterVO;
import com.thtf.face_recognition.domain.alarmMng.alarmDetail.AlarmDetailDO;
import com.thtf.face_recognition.domain.alarmMng.enums.ImageTypeEnum;
import com.thtf.face_recognition.domain.alarmMng.valueObject.FaceRect;
import com.thtf.face_recognition.domain.alarmMng.valueObject.PersonInfo;
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

    MegviiAlarmData toAlarmDetail(AlarmDetailDO alarmDetailDO);

    default String toPersonInfoString(PersonInfo personInfo){
        return null == personInfo ? null : personInfo.toJsonString();
    }

    default String toFaceRectString(FaceRect faceRect){
        return null == faceRect ? null : faceRect.toJsonString();
    }

    default Integer toImageTypeDesc(ImageTypeEnum imageTypeEnum){
        return null == imageTypeEnum ? null : imageTypeEnum.getId();
    }
}