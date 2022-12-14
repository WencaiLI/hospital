package com.thtf.face_recognition.controller;

import com.thtf.common.response.JsonResult;
import com.thtf.face_recognition.dto.DisplayParamDTO;
import com.thtf.face_recognition.dto.FaceRecognitionPointDTO;
import com.thtf.face_recognition.dto.MegviiItemEventDTO;
import com.thtf.face_recognition.dto.MegviiPage;
import com.thtf.face_recognition.service.FaceRecognitionService;
import com.thtf.face_recognition.service.ManufacturerApiService;
import com.thtf.face_recognition.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
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


    @Autowired
    @Qualifier("Megvii")
    private ManufacturerApiService manufacturerApiService;


    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/7
     * @Param: sysCode: 子系统编码
     * @Return: com.thtf.common.response.JsonResult<com.thtf.face_recognition.vo.FaceRecognitionDisplayVO>
     */
    @PostMapping("/display")
    public JsonResult<FaceRecognitionDisplayVO> getDisplayInfo(@RequestBody DisplayParamDTO displayParamDTO){
        return JsonResult.querySuccess(faceRecognitionService.getDisplayInfo(displayParamDTO));
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/7
     * @Param: paramVO:
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.face_recognition.vo.FaceRecognitionItemResultVO>>
     */
    @PostMapping("/listFaceRecognitionItem")
    public JsonResult<PageInfoVO> listFaceRecognitionItem(@RequestBody FaceRecognitionItemParamVO paramVO){
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
    public JsonResult<MegviiPage<FaceRecognitionAlarmResultVO>> listFaceRecognitionAlarm(@RequestBody FaceRecognitionAlarmParamVO paramVO){
        return JsonResult.querySuccess(manufacturerApiService.listFaceRecognitionAlarm(paramVO));
    }


    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/7
     * @Param: paramVO:
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.face_recognition.vo.FaceRecognitionItemResultVO>>
     */
    @PostMapping("/listFaceRecognitionFault")
    public JsonResult<MegviiPage<FaceRecognitionFaultResultVO>> listFaceRecognitionFault(@RequestBody FaceRecognitionAlarmParamVO paramVO){
        MegviiPage<FaceRecognitionFaultResultVO> result = new MegviiPage<>();
        result.setTotal(100);
        result.setPageNum(paramVO.getPageNumber());
        result.setPageSize(paramVO.getPageSize());
        List<FaceRecognitionFaultResultVO> list = new ArrayList<>();
        FaceRecognitionFaultResultVO resultVO = new FaceRecognitionFaultResultVO();
        resultVO.setIpAddress("127.0.0.1");
        resultVO.setStayTime("0天1时");
        resultVO.setAlarmLevel(1);
        resultVO.setItemCode("RLSB_TYPE_1");
        resultVO.setItemName("人脸识别设备_1");
        resultVO.setAreaName("区域名名称");
        resultVO.setAreaCode("区域编码");
        resultVO.setEye(new ArrayList<Integer>(Arrays.asList(new Integer[]{1, 2, 3})));
        resultVO.setCenter(new ArrayList<Integer>(Arrays.asList(new Integer[]{1, 2, 3})));
        list.add(resultVO);
        result.setList(list);
        return JsonResult.querySuccess(result);
        // return JsonResult.querySuccess(manufacturerApiService.listFaceRecognitionFault(paramVO));
    }

    /**
     * 获取设备点位信息
     */
    @GetMapping("/monitor_point_info")
    public JsonResult<FaceRecognitionPointDTO> getMonitorPointInfo(@RequestParam("itemCode") String itemCode){
        return JsonResult.querySuccess(faceRecognitionService.getMonitorPointInfo(itemCode));
    }

    /**
     * @Author: liwencai
     * @Description: 单设备事件
     * @Date: 2022/12/14
     * @Param itemCode:
     * @Param pageNumber:
     * @Param pageSize:
     * @Return: com.thtf.common.response.JsonResult<com.thtf.face_recognition.dto.MegviiPage<com.thtf.face_recognition.dto.MegviiItemEventDTO>>
     */
    @GetMapping("/item_event")
    public JsonResult<MegviiPage<MegviiItemEventDTO>> listItemEventByItemCode(@RequestParam("itemCode") String itemCode,
                                                                              @RequestParam("pageNumber") Integer pageNumber,
                                                                              @RequestParam("pageSize") Integer pageSize){
        return JsonResult.querySuccess(manufacturerApiService.listItemEventByItemCode(itemCode,pageNumber,pageSize));
    }

}
