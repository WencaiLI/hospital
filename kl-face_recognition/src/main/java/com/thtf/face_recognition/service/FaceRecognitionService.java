package com.thtf.face_recognition.service;

import com.thtf.face_recognition.vo.*;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/11/7 09:55
 * @Description:
 */
public interface FaceRecognitionService {
    FaceRecognitionDisplayVO getDisplayInfo(String sysCode);

    List<FaceRecognitionItemResultVO> listFaceRecognitionItem(FaceRecognitionItemParamVO paramVO);

//    List<FaceRecognitionAlarmResultVO> listFaceRecognitionAlarm(FaceRecognitionAlarmParamVO paramVO);
}
