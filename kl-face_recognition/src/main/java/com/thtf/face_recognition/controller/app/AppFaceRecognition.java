package com.thtf.face_recognition.controller.app;

import com.thtf.common.dto.adminserver.ResultPage;
import com.thtf.common.dto.alarmserver.AppAlarmRecordDTO;
import com.thtf.common.dto.alarmserver.ItemAlarmDetailDTO;
import com.thtf.common.dto.alarmserver.ItemListVisitVO;
import com.thtf.common.dto.alarmserver.ListAlarmPageParamDTO;
import com.thtf.common.dto.itemserver.PageInfoVO;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.face_recognition.dto.DisplayParamDTO;
import com.thtf.face_recognition.service.FaceRecognitionService;
import com.thtf.face_recognition.service.ManufacturerApiService;
import com.thtf.face_recognition.vo.FaceRecognitionDisplayVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: liwencai
 * @Date: 2023/2/17 18:47
 * @Description:
 */
@RequestMapping("/face_recognition/app")
@RestController
public class AppFaceRecognition {

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    @Resource
    private ItemAPI itemAPI;

    @Resource
    private AlarmAPI alarmAPI;

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
     * @Description: 设备列表查看（根据设备状态）
     * @Date: 2022/8/16
     * @Param param:
     * @return: com.thtf.common.response.JsonResult
     */
    @PostMapping("/item_info")
    public JsonResult<PageInfoVO<ItemAlarmDetailDTO>> getItemInfoByItemStatusAndType(@RequestBody ItemListVisitVO param){
        try {
            param.setCategory("1");
            return itemAPI.getItemInfoByItemStatusAndType(param);
        }catch (Exception e){
            return  JsonResult.error("服务器错误");
        }
    }

    /**
     * @Author: liwencai
     * @Description: 未处理报警
     * @Date: 2022/12/12
     * @Param param:
     * @Return: com.thtf.common.response.JsonResult
     */
    @PostMapping("/alarm_unhandled")
    public JsonResult<ResultPage<AppAlarmRecordDTO>> getUnhandledAlarm(@RequestBody ListAlarmPageParamDTO param){
        param.setStatus(0);
        return alarmAPI.listAlarm(param);
    }

    /**
     * @Author: liwencai
     * @Description: 已处理报警 应该跟未处理报警
     * @Date: 2022/12/12
     * @Param param:
     * @Return: com.thtf.common.response.JsonResult<com.github.pagehelper.PageInfo<com.thtf.common.dto.alarmserver.AppAlarmRecordDTO>>
     */
    @PostMapping("/alarm_processed")
    public JsonResult<ResultPage<AppAlarmRecordDTO>> getProcessedAlarm(@RequestBody ListAlarmPageParamDTO param){
        param.setStatus(1);
        return alarmAPI.listAlarm(param);
    }
}
