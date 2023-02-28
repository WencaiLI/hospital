package com.thtf.elevator.controller.app;

import com.thtf.common.dto.adminserver.ResultPage;
import com.thtf.common.dto.alarmserver.AppAlarmRecordDTO;
import com.thtf.common.dto.alarmserver.ItemAlarmDetailDTO;
import com.thtf.common.dto.alarmserver.ItemListVisitVO;
import com.thtf.common.dto.alarmserver.ListAlarmPageParamDTO;
import com.thtf.common.dto.itemserver.PageInfoVO;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.elevator.dto.DisplayInfoDTO;
import com.thtf.elevator.service.ElevatorAppService;
import com.thtf.elevator.service.ElevatorService;
import com.thtf.elevator.vo.AppAlarmInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2023/2/28 18:47
 * @Description:
 */
@RestController
@RequestMapping(value ="/elevator/app/")
@Slf4j
public class AppElevatorController {
    @Resource
    private ElevatorService elevatorService;

    @Resource
    private ElevatorAppService elevatorAppService;

    @Resource
    private ItemAPI itemAPI;

    @Resource
    private AlarmAPI alarmAPI;
    /**
     * @Author: liwencai
     * @Description: 前端展示界面数据
     * @Date: 2022/9/2
     * @Param sysCode: 子系统编码
     * @return: com.thtf.common.response.JsonResult<com.thtf.elevator.dto.DisplayInfoDTO>
     */
    @PostMapping("/displayInfo")
    JsonResult<List<DisplayInfoDTO>> displayInfo(@RequestParam(value ="sysCode")String sysCode,
                                                 @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                 @RequestParam(value = "areaCode",required = false) String areaCode){
        return JsonResult.querySuccess(elevatorAppService.displayInfo(sysCode,buildingCodes,areaCode));
    }

    /**
     * @Author: liwencai
     * @Description: 前端展示界面数据
     * @Date: 2022/9/2
     * @Param sysCode: 子系统编码
     * @return: com.thtf.common.response.JsonResult<com.thtf.elevator.dto.DisplayInfoDTO>
     */
    @PostMapping("/alarmInfo")
    JsonResult<AppAlarmInfoVO> getAlarmInfo(@RequestParam(value ="sysCode")String sysCode,
                                            @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                            @RequestParam(value = "areaCode",required = false) String areaCode){
        return JsonResult.querySuccess(elevatorAppService.getAlarmInfo(sysCode,buildingCodes,areaCode));
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
