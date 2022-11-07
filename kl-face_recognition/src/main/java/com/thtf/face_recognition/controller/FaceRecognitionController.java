package com.thtf.face_recognition.controller;

import com.thtf.common.response.JsonResult;
import com.thtf.face_recognition.service.FaceRecognitionService;
import com.thtf.face_recognition.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/11/7 09:57
 * @Description:
 */
@RequestMapping("/face_recognition")
@RestController
public class FaceRecognitionController {

    @Autowired
    private FaceRecognitionService faceRecognitionService;


    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/7
     * @Param: sysCode: 子系统编码
     * @Return: com.thtf.common.response.JsonResult<com.thtf.face_recognition.vo.FaceRecognitionDisplayVO>
     */
    @GetMapping("/display")
    public JsonResult<FaceRecognitionDisplayVO> getDisplayInfo(@RequestParam("sysCode")String sysCode){
        return JsonResult.querySuccess(faceRecognitionService.getDisplayInfo(sysCode));
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/7
     * @Param: paramVO:
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.face_recognition.vo.FaceRecognitionItemResultVO>>
     */
    @PostMapping("/listFaceRecognitionItem")
    public JsonResult<List<FaceRecognitionItemResultVO>> listFaceRecognitionItem(@RequestBody FaceRecognitionItemParamVO paramVO){
        return JsonResult.querySuccess(faceRecognitionService.listFaceRecognitionItem(paramVO));
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/7
     * @Param: paramVO:
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.face_recognition.vo.FaceRecognitionItemResultVO>>
     */
    @PostMapping("/listFaceRecognitionAlarm")
    public JsonResult<List<FaceRecognitionAlarmResultVO>> listFaceRecognitionAlarm(@RequestBody FaceRecognitionAlarmParamVO paramVO){
        return JsonResult.querySuccess(faceRecognitionService.listFaceRecognitionAlarm(paramVO));
    }


}
