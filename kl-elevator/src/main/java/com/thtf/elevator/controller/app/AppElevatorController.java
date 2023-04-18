package com.thtf.elevator.controller.app;

import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.adminserver.ResultPage;
import com.thtf.common.dto.alarmserver.AppAlarmRecordDTO;
import com.thtf.common.dto.alarmserver.ListAlarmPageParamDTO;
import com.thtf.common.dto.itemserver.CodeAndNameDTO;
import com.thtf.common.dto.itemserver.TblItemDTO;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.elevator.dto.DisplayInfoDTO;
import com.thtf.elevator.service.ElevatorAppService;
import com.thtf.elevator.service.ElevatorService;
import com.thtf.elevator.vo.AppAlarmInfoVO;
import com.thtf.elevator.vo.AppItemSortDTO;
import com.thtf.elevator.vo.AppItemSortVO;
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
@RequestMapping(value ="/elevator/app")
@Slf4j
public class AppElevatorController {

    @Resource
    private ElevatorAppService elevatorAppService;

    @Resource ElevatorService elevatorService;

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
    public JsonResult<PageInfo<AppItemSortVO>> getItemInfoByItemStatusAndType(@RequestBody AppItemSortDTO param){
        return JsonResult.querySuccess(elevatorAppService.listItem(param));
    }


    /**
     * itemcode 查询item信息 包含关联的摄像头
     * @param itemCode
     * @return
     * @author lvgch
     * @date 2022-11-10
     */
    @GetMapping("/getItemInfoByItemCode")
    public JsonResult<TblItemDTO> getItemDTOByItemCode(@RequestParam("itemCode")String  itemCode) {
        return this.itemAPI.getItemDTOByItemCode(itemCode).getBody();
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

    /**
     * @Author: liwencai
     * @Description: 获取电梯类别
     * @Date: 2023/3/2
     * @Param sysCode: 子系统编码
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.common.dto.itemserver.CodeAndNameDTO>>
     */
    @GetMapping("/getElevatorType")
    public JsonResult<List<CodeAndNameDTO>> getElevatorType(@RequestParam("sysCode") String sysCode){
        return JsonResult.querySuccess(elevatorService.listItemTypeLeaf(sysCode));
    }

}
