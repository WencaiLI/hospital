package com.thtf.face_recognition.service;

import com.thtf.face_recognition.vo.FaceRecognitionAlarmParamVO;
import com.thtf.face_recognition.vo.FaceRecognitionAlarmResultVO;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/11/13 17:55
 * @Description: 制造商服务接口
 */
public interface ManufacturerApiService {

    List<FaceRecognitionAlarmResultVO> listFaceRecognitionAlarm(FaceRecognitionAlarmParamVO paramVO);
}
