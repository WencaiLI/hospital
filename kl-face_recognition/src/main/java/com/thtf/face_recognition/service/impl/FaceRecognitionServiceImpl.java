package com.thtf.face_recognition.service.impl;

import com.thtf.face_recognition.service.FaceRecognitionService;
import com.thtf.face_recognition.vo.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/11/7 09:56
 * @Description:
 */
@Service("face_recognition")
public class FaceRecognitionServiceImpl implements FaceRecognitionService {
    @Override
    public FaceRecognitionDisplayVO getDisplayInfo(String sysCode) {
        return null;
    }

    @Override
    public List<FaceRecognitionItemResultVO> listFaceRecognitionItem(FaceRecognitionItemParamVO paramVO) {
        return null;
    }

    @Override
    public List<FaceRecognitionAlarmResultVO> listFaceRecognitionAlarm(FaceRecognitionAlarmParamVO paramVO) {
        return null;
    }
}
