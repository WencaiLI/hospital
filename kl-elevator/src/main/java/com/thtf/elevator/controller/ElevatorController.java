package com.thtf.elevator.controller;

import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.itemserver.CodeAndNameDTO;
import com.thtf.common.response.JsonResult;
import com.thtf.elevator.dto.DisplayInfoDTO;
import com.thtf.elevator.dto.ElevatorAlarmResultDTO;
import com.thtf.elevator.dto.ElevatorInfoResultDTO;
import com.thtf.elevator.dto.ItemFaultStatisticsDTO;
import com.thtf.elevator.service.ElevatorService;
import com.thtf.elevator.vo.QueryItemParamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
    private ElevatorService elevatorService;

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
     * @Description: 获取末级设备类别
     * @Date: 2022/12/1
     * @Param sysCode: 子系统编码
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.common.dto.itemserver.CodeAndNameDTO>>
     */
    @GetMapping("/getItemType")
    public JsonResult<List<CodeAndNameDTO>> getItemType(@RequestParam("sysCode")String sysCode){
        return JsonResult.querySuccess(elevatorService.listItemTypeLeaf(sysCode));
    }

    /**
     * @Author: liwencai
     * @Description: 获取电梯的设备参数
     * @Date: 2022/9/5
     * @Param itemCodeList: 设备编码集
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.elevator.dto.ElevatorSwitchParameterDTO>>
     */
    @PostMapping(value = "/getParameterInfo")
    public JsonResult<List<ElevatorInfoResultDTO>> listElevatorItemByCodeList(@RequestParam("itemCodeList")List<String> itemCodeList){
        return JsonResult.querySuccess(elevatorService.listElevatorItemByCodeList(itemCodeList));
    }


    /**
     * @Author: liwencai
     * @Description: 获取电梯信息
     * @Date: 2022/9/5
     * @Param sysCode: 子系统编码
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: com.thtf.common.response.JsonResult<com.github.pagehelper.PageInfo<java.util.List<com.thtf.elevator.dto.ElevatorInfoResultDTO>>>
     */
    @PostMapping("/getAllElevatorPage")
    public JsonResult<PageInfo<ElevatorInfoResultDTO>> listElevatorItemPage(@RequestParam("sysCode")String sysCode,
                                                                          @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                                          @RequestParam(value = "areaCode",required = false) String areaCode,
                                                                          @RequestParam(value = "itemTypeCode",required = false) String itemTypeCode,
                                                                          @RequestParam(value = "state",required = false) Integer state,
                                                                          @RequestParam(value = "pageNumber",required = false)Integer pageNumber,
                                                                          @RequestParam(value = "pageSize",required = false)Integer pageSize){
        return JsonResult.querySuccess(elevatorService.listElevatorItemPage(sysCode,buildingCodes,areaCode,itemTypeCode,state,pageNumber,pageSize));
    }



    /**
     * @Author: liwencai
     * @Description: 查看电梯故障报警信息
     * @Date: 2022/9/5
     * @Param sysCode: 子系统编码
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: com.thtf.common.response.JsonResult<com.github.pagehelper.PageInfo<com.thtf.elevator.dto.ElevatorAlarmResultDTO>>
     */
    @PostMapping("/getAllAlarmPage")
    public JsonResult<PageInfo<ElevatorAlarmResultDTO>> listElevatorAlarmPage(@RequestParam("sysCode") String sysCode,
                                                                        @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                                        @RequestParam(value = "areaCode",required = false) String areaCode,
                                                                        @RequestParam("alarmCategory") Integer alarmCategory,
                                                                        @RequestParam(value = "itemTypeCode",required = false) String itemTypeCode,
                                                                        @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
                                                                        @RequestParam(value = "pageSize",required = false) Integer pageSize){
        return JsonResult.querySuccess(elevatorService.listElevatorAlarmPage(sysCode,buildingCodes,areaCode,itemTypeCode,alarmCategory,pageNumber,pageSize));
    }

    /**
     * @Author: liwencai
     * @Description: 报警情况统计
     * @Date: 2022/9/5
     * @Param sysCode: 子系统编码
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.elevator.dto.KeyValueDTO>>
     */
    @PostMapping("/getItemFaultStatistics")
    public JsonResult<ItemFaultStatisticsDTO> getItemFaultStatistics(@RequestParam("sysCode")String sysCode,
                                                                     @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                                     @RequestParam(value = "areaCode",required = false) String areaCode,
                                                                     @RequestParam(value = "itemTypeCode",required = false) String itemTypeCode,
                                                                     @RequestParam("startTime")String startTime,
                                                                     @RequestParam("endTime")String endTime){
        return JsonResult.querySuccess(elevatorService.getItemFaultStatistics(sysCode, buildingCodes, areaCode,itemTypeCode,startTime, endTime));
    }


    /**
     * @Author: liwencai
     * @Description: 查询设备信息
     * @Date: 2023/3/8
     * @Return: com.thtf.common.response.JsonResult<java.util.List>
     */
    @PostMapping("/queryItem")
    public JsonResult<List<ElevatorInfoResultDTO>> queryItem(@RequestBody QueryItemParamVO queryItemParamVO){
       return JsonResult.querySuccess(elevatorService.queryItem(queryItemParamVO));
    }
}
