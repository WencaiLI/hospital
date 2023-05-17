package com.thtf.face_recognition.application;

import com.thtf.face_recognition.application.dto.DisplayParamDTO;
import com.thtf.face_recognition.application.dto.FaceRecognitionPointDTO;
import com.thtf.face_recognition.application.vo.FaceRecognitionDisplayVO;
import com.thtf.face_recognition.application.vo.FaceRecognitionItemParamVO;
import com.thtf.face_recognition.application.vo.FaceRecognitionItemResultVO;
import com.thtf.face_recognition.application.vo.PageInfoVO;

/**
 * @Author: liwencai
 * @Date: 2022/11/7 09:55
 * @Description:
 */
public interface FaceRecognitionService {

    /**
     * @Author: liwencai
     * @Description: 获取设备展示信息
     * @Date: 2023/11/7
     * @Param displayParamDTO:
     * @Return: com.thtf.face_recognition.application.vo.FaceRecognitionDisplayVO
     */
    FaceRecognitionDisplayVO getDisplayInfo(DisplayParamDTO displayParamDTO);

    /**
     * @Author: liwencai
     * @Description: 获取人脸识别设备列表
     * @Date: 2022/11/7
     * @Param paramVO:
     * @Return: com.thtf.face_recognition.application.vo.PageInfoVO
     */
    PageInfoVO<FaceRecognitionItemResultVO> listFaceRecognitionItem(FaceRecognitionItemParamVO paramVO);

    /**
     * @Author: liwencai
     * @Description: 获取监测点位信息
     * @Date: 2022/11/7
     * @Param itemCode:
     * @Return: com.thtf.face_recognition.application.dto.FaceRecognitionPointDTO
     */
    FaceRecognitionPointDTO getMonitorPointInfo(String itemCode);

}
