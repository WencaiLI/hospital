package com.thtf.face_recognition.service.impl;

import com.thtf.face_recognition.service.FaceRecognitionService;
import com.thtf.face_recognition.service.ManufacturerApiService;
import com.thtf.face_recognition.vo.FaceRecognitionAlarmParamVO;
import com.thtf.face_recognition.vo.FaceRecognitionAlarmResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/11/13 17:57
 * @Description: 人脸识别API（盘古2.0）,旷世科技接口实现
 */
@Service("Megvii")
public class MegviiApiServiceImpl implements ManufacturerApiService {
    @Autowired
    private FaceRecognitionService faceRecognitionService;

    @Override
    public List<FaceRecognitionAlarmResultVO> listFaceRecognitionAlarm(FaceRecognitionAlarmParamVO paramVO) {
        return null;
    }

}
