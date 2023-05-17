package com.thtf.face_recognition.adapter.driving.web;

import com.thtf.common.response.JsonResult;
import com.thtf.face_recognition.application.FaceRecognitionService;
import com.thtf.face_recognition.application.ManufacturerApiService;
import com.thtf.face_recognition.application.dto.DisplayParamDTO;
import com.thtf.face_recognition.application.dto.FaceRecognitionPointDTO;
import com.thtf.face_recognition.application.dto.MegviiItemEventDTO;
import com.thtf.face_recognition.application.dto.MegviiPage;
import com.thtf.face_recognition.application.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

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


    @Autowired
    @Qualifier("Megvii")
    private ManufacturerApiService manufacturerApiService;


    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/7
     * @Param: sysCode: 子系统编码
     * @Return: com.thtf.common.response.JsonResult<com.thtf.face_recognition.application.vo.FaceRecognitionDisplayVO>
     */
    @PostMapping("/display")
    public JsonResult<FaceRecognitionDisplayVO> getDisplayInfo(@RequestBody DisplayParamDTO displayParamDTO) {
        return JsonResult.querySuccess(faceRecognitionService.getDisplayInfo(displayParamDTO));
    }

    /**
     * @Author: liwencai
     * @Description: 获取人脸识别设备列表
     * @Date: 2022/11/7
     * @Param: paramVO:
     * @Return: com.thtf.common.response.JsonResult<java.util.List < com.thtf.face_recognition.application.vo.FaceRecognitionItemResultVO>>
     */
    @PostMapping("/listFaceRecognitionItem")
    public JsonResult<PageInfoVO<FaceRecognitionItemResultVO>> listFaceRecognitionItem(@RequestBody FaceRecognitionItemParamVO paramVO) {
        return JsonResult.querySuccess(faceRecognitionService.listFaceRecognitionItem(paramVO));
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/7
     * @Param: paramVO:
     * @Return: com.thtf.common.response.JsonResult<java.util.List < com.thtf.face_recognition.application.vo.FaceRecognitionItemResultVO>>
     */
    @PostMapping("/listFaceRecognitionAlarm")
    public JsonResult<MegviiPage<FaceRecognitionAlarmResultVO>> listFaceRecognitionAlarm(@RequestBody FaceRecognitionAlarmParamVO paramVO) {
        return JsonResult.querySuccess(manufacturerApiService.listFaceRecognitionAlarm(paramVO));
    }


    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/7
     * @Param: paramVO:
     * @Return: com.thtf.common.response.JsonResult<java.util.List < com.thtf.face_recognition.application.vo.FaceRecognitionItemResultVO>>
     */
    @PostMapping("/listFaceRecognitionFault")
    public JsonResult<MegviiPage<FaceRecognitionFaultResultVO>> listFaceRecognitionFault(@RequestBody FaceRecognitionAlarmParamVO paramVO) {
        return JsonResult.querySuccess(manufacturerApiService.listFaceRecognitionFault(paramVO));
    }

    /**
     * 获取设备点位信息
     */
    @GetMapping("/monitor_point_info")
    public JsonResult<FaceRecognitionPointDTO> getMonitorPointInfo(@RequestParam("itemCode") String itemCode) {
        return JsonResult.querySuccess(faceRecognitionService.getMonitorPointInfo(itemCode));
    }

    /**
     * @Author: liwencai
     * @Description: 单设备事件
     * @Date: 2022/12/14
     * @Param itemCode:
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @Return: com.thtf.common.response.JsonResult<com.thtf.face_recognition.application.dto.MegviiPage < com.thtf.face_recognition.application.dto.MegviiItemEventDTO>>
     */
    @GetMapping("/item_event")
    public JsonResult<MegviiPage<MegviiItemEventDTO>> listItemEventByItemCode(@RequestParam("itemCode") String itemCode,
                                                                              @RequestParam("pageNumber") Integer pageNumber,
                                                                              @RequestParam("pageSize") Integer pageSize) {
        return JsonResult.querySuccess(manufacturerApiService.listItemEventByItemCode(itemCode, pageNumber, pageSize));
    }

}
