package com.thtf.face_recognition.service;

import com.thtf.face_recognition.dto.DisplayParamDTO;
import com.thtf.face_recognition.vo.*;

/**
 * @Author: liwencai
 * @Date: 2022/11/7 09:55
 * @Description:
 */
public interface FaceRecognitionService {

    FaceRecognitionDisplayVO getDisplayInfo(DisplayParamDTO displayParamDTO);

    PageInfoVO listFaceRecognitionItem(FaceRecognitionItemParamVO paramVO);

//    List<FaceRecognitionAlarmResultVO> listFaceRecognitionAlarm(FaceRecognitionAlarmParamVO paramVO);
}
