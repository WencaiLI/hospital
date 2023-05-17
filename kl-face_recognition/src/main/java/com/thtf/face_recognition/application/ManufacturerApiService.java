package com.thtf.face_recognition.application;

import com.thtf.face_recognition.application.dto.MegviiItemEventDTO;
import com.thtf.face_recognition.application.dto.MegviiPage;
import com.thtf.face_recognition.application.vo.FaceRecognitionAlarmParamVO;
import com.thtf.face_recognition.application.vo.FaceRecognitionAlarmResultVO;
import com.thtf.face_recognition.application.vo.FaceRecognitionFaultResultVO;

/**
 * @Author: liwencai
 * @Date: 2022/11/13 17:55
 * @Description: 制造商服务接口
 */
public interface ManufacturerApiService {

    MegviiPage<FaceRecognitionAlarmResultVO> listFaceRecognitionAlarm(FaceRecognitionAlarmParamVO paramVO);

    MegviiPage<FaceRecognitionFaultResultVO> listFaceRecognitionFault(FaceRecognitionAlarmParamVO paramVO);

    MegviiPage<MegviiItemEventDTO> listItemEventByItemCode(String itemCode, Integer pageNumber, Integer pageSize);
}
