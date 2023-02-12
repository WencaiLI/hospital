package com.thtf.elevator.controller;

import com.thtf.common.dto.alarmserver.ItemAlarmNumberInfo;
import com.thtf.common.dto.itemserver.CodeAndNameDTO;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.response.JsonResult;
import com.thtf.elevator.dto.DisplayInfoDTO;
import com.thtf.elevator.dto.ElevatorInfoResultDTO;
import com.thtf.elevator.dto.ItemFaultStatisticsDTO;
import com.thtf.elevator.service.ElevatorService;
import com.thtf.elevator.vo.PageInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/2 10:20
 * @Description:
 */
@RestController
@RequestMapping(value ="/elevator")
@Slf4j
public class ElevatorController {

    @Resource
    ElevatorService elevatorService;

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
        return JsonResult.querySuccess(elevatorService.displayInfo(sysCode,buildingCodes,areaCode));
    }

    /**
     * @Author: liwencai
     * @Description: 获取电梯的设备参数
     * @Date: 2022/9/5
     * @Param itemCodeList:
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.elevator.dto.ElevatorSwitchParameterDTO>>
     */
    @PostMapping(value = "/getParameterInfo")
    public JsonResult<List<ElevatorInfoResultDTO>> getParameterInfo(@RequestParam("itemCodeList")List<String> itemCodeList,
                                                                    @RequestParam("isNeedAreaName" )Boolean isNeedAreaName){
        return JsonResult.querySuccess(elevatorService.itemCodeList(itemCodeList,isNeedAreaName));
    }


    /**
     * @Author: liwencai
     * @Description: 获取电梯信息
     * @Date: 2022/9/5
     * @Param sysCode:
     * @Param pageNumber:
     * @Param pageSize:
     * @return: com.thtf.common.response.JsonResult<com.github.pagehelper.PageInfo<java.util.List<com.thtf.elevator.dto.ElevatorInfoResultDTO>>>
     */
    @PostMapping("/getAllElevatorPage")
    public JsonResult<PageInfoVO> getAllElevatorPage(@RequestParam("sysCode")String sysCode,
                                                     @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                     @RequestParam(value = "areaCode",required = false) String areaCode,
                                                     @RequestParam(value = "itemTypeCode",required = false) String itemTypeCode,
                                                     @RequestParam(value = "state",required = false) Integer state,
                                                     @RequestParam(value = "pageNumber",required = false)Integer pageNumber,
                                                     @RequestParam(value = "pageSize",required = false)Integer pageSize){
        return JsonResult.querySuccess(elevatorService.getAllElevatorPage(sysCode,buildingCodes,areaCode,itemTypeCode,state,pageNumber,pageSize));
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/12/1
     * @Param sysCode: 子系统编码
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.common.dto.itemserver.CodeAndNameDTO>>
     */
    @GetMapping("/getItemType")
    public JsonResult<List<CodeAndNameDTO>> getItemType(@RequestParam("sysCode")String sysCode){
        return JsonResult.querySuccess(elevatorService.getItemType(sysCode));
    }

    /**
     * @Author: liwencai
     * @Description: 查看电梯故障报警信息
     * @Date: 2022/9/5
     * @Param sysCode:
     * @Param pageNumber:
     * @Param pageSize:
     * @return: com.thtf.common.response.JsonResult<com.github.pagehelper.PageInfo<com.thtf.elevator.dto.ElevatorAlarmResultDTO>>
     */
    @PostMapping("/getAllAlarmPage")
    public JsonResult<PageInfoVO> getAllAlarmPage(@RequestParam("sysCode") String sysCode,
                                                  @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                  @RequestParam(value = "areaCode",required = false) String areaCode,
                                                  @RequestParam("alarmCategory") Integer alarmCategory,
                                                  @RequestParam(value = "itemTypeCode",required = false) String itemTypeCode,
                                                  @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
                                                  @RequestParam(value = "pageSize",required = false) Integer pageSize){
        return JsonResult.querySuccess(elevatorService.getAllAlarmPage(sysCode,buildingCodes,areaCode,itemTypeCode,alarmCategory,pageNumber,pageSize));
    }

    /**
     * @Author: liwencai
     * @Description: 报警情况统计
     * @Date: 2022/9/5
     * @Param sysCode:
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.elevator.dto.KeyValueDTO>>
     */
    @PostMapping("/getItemFaultStatistics")
    public JsonResult<ItemFaultStatisticsDTO> getItemFaultStatistics(@RequestParam("sysCode")String sysCode,
                                                                     @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                                     @RequestParam(value = "areaCode",required = false) String areaCode,
                                                                     @RequestParam("startTime")String startTime,
                                                                     @RequestParam("endTime")String endTime){
        List<ItemAlarmNumberInfo> itemFaultStatistics = elevatorService.getItemFaultStatistics(sysCode, buildingCodes, areaCode,startTime, endTime);

        if(null == itemFaultStatistics || itemFaultStatistics.size() == 0){
            return JsonResult.querySuccess(null);
        }
        ItemFaultStatisticsDTO result = new ItemFaultStatisticsDTO();
        List<String> itemNameList = new ArrayList<>();
        List<Integer> monitorAlarmNumberList = new ArrayList<>();
        List<Integer> malfunctionAlarmNumberList = new ArrayList<>();
        for (ItemAlarmNumberInfo alarmNumberInfo : itemFaultStatistics) {
            itemNameList.add(alarmNumberInfo.getItemName());
            monitorAlarmNumberList.add(alarmNumberInfo.getMonitorAlarmNumber());
            malfunctionAlarmNumberList.add(alarmNumberInfo.getMalfunctionAlarmNumber());
        }
        result.setItemName(itemNameList);
        result.setMonitorAlarmNumber(monitorAlarmNumberList);
        result.setMalfunctionAlarmNumber(malfunctionAlarmNumberList);
        return JsonResult.querySuccess(result);
    }
}
