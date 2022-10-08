package com.thtf.elevator.controller;

import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.alarmserver.ItemAlarmNumberInfo;
import com.thtf.common.dto.itemserver.ItemNestedParameterVO;
import com.thtf.common.entity.adminserver.TblBuildingArea;
import com.thtf.common.entity.alarmserver.TblAlarmRecord;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.elevator.dto.DisplayInfoDTO;
import com.thtf.elevator.dto.ElevatorInfoResultDTO;
import com.thtf.elevator.dto.FloorInfoDTO;
import com.thtf.elevator.service.ElevatorService;
import com.thtf.elevator.vo.PageInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Resource
    AlarmAPI alarmAPI;

    /**
     * @Author: liwencai
     * @Description: 获取楼层信息
     * @Date: 2022/9/22
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.common.entity.adminserver.TblBuildingArea>>
     */
    @GetMapping("/getFloorInfo")
    public JsonResult<List<FloorInfoDTO>> getFloorInfo(){
        try {
            return JsonResult.success(elevatorService.getFloorInfo());
        }catch (Exception e){
            return JsonResult.error("服务器错误");
        }
    }

    /**
     * @Author: liwencai
     * @Description: 获取当前最新报警（当日）
     * @Date: 2022/9/5
     * @Param sysCode:
     * @Param pageNumber:
     * @Param pageSize:
     * @return: com.thtf.common.response.JsonResult
     */
    @GetMapping(value = "/getAlarmUnhandledToday")
    public JsonResult alarmUnhandledToday(@RequestParam("sysCode") String sysCode,
                                          @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
                                          @RequestParam(value = "pageSize",required = false) Integer pageSize){
        Map<String, Object> map = new HashMap<>();
        map.put("sysCode",sysCode);
        if(null != pageNumber){
            map.put("pageNumber",pageNumber);
        }
        if (null != pageSize){
            map.put("pageSize",pageSize);
        }
        try {
            return alarmAPI.alarmUnhandledToday(map);
        }catch (Exception e){
            log.error(e.getMessage());
            return JsonResult.error("服务器错误");
        }
    }

    /**
     * @Author: liwencai
     * @Description: 前端展示界面数据
     * @Date: 2022/9/2
     * @Param sysCode:
     * @Param itemType:
     * @return: com.thtf.common.response.JsonResult<com.thtf.elevator.dto.DisplayInfoDTO>
     */
    @PostMapping("/displayInfo")
    JsonResult<List<DisplayInfoDTO>> displayInfo(@RequestParam(value ="sysCode")String sysCode,
                                                @RequestParam(value ="itemType")String itemType){
        return JsonResult.success(elevatorService.displayInfo(sysCode,itemType));
    }

    /**
     * @Author: liwencai
     * @Description: 获取报警数量
     * @Date: 2022/9/5
     * @Param sysCode:
     * @return: com.thtf.common.response.JsonResult<com.thtf.elevator.dto.DisplayInfoDTO>
     */
    @PostMapping("/alarmNumber")
    JsonResult<Integer> alarmNumber(@RequestParam("sysCode")String sysCode){
        return JsonResult.success(elevatorService.alarmNumber(sysCode));
    }

    /**
     * @Author: liwencai
     * @Description: 报警处置
     * @Date: 2022/9/2
     * @Param param:
     * @return: com.thtf.common.response.JsonResult<java.lang.Boolean>
     */
    @PutMapping(value = "/disposalAlarm")
    public JsonResult<Boolean> alarmDisposal(@RequestBody TblAlarmRecord param){
        try {
            alarmAPI.handleAlarm(param);
            return JsonResult.success();
        }catch (Exception e){
            return JsonResult.error("操作失败");
        }
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
        return JsonResult.success(elevatorService.itemCodeList(itemCodeList,isNeedAreaName));
    }


    /**
     * @Author: liwencai
     * @Description: 查看关联设备信息
     * @Date: 2022/9/5
     * @Param relationType:
     * @Param itemCode:
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.common.entity.itemserver.TblItem>>
     */
    @PostMapping(value = "/getItemRelInfo")
    public JsonResult<List<TblItem>> getItemRelInfo(@RequestParam("relationType") String relationType,
                                              @RequestParam("itemCode") String itemCode){
        return JsonResult.success(elevatorService.getItemRelInfo(relationType,itemCode));
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
                                                     @RequestParam(value = "itemTypeCode",required = false) String itemTypeCode,
                                                     @RequestParam(value = "pageNumber",required = false)Integer pageNumber,
                                                     @RequestParam(value = "pageSize",required = false)Integer pageSize){
        return JsonResult.success(elevatorService.getAllElevatorPage(sysCode,itemTypeCode,pageNumber,pageSize));
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
                                                           @RequestParam(value = "itemTypeCode",required = false) String itemTypeCode,
                                                           @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
                                                           @RequestParam(value = "pageSize",required = false) Integer pageSize){
        return JsonResult.success(elevatorService.getAllAlarmPage(sysCode,itemTypeCode,pageNumber,pageSize));
    }

    /**
     * @Author: liwencai
     * @Description: 报警情况统计
     * @Date: 2022/9/5
     * @Param sysCode:
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.elevator.dto.KeyValueDTO>>
     */
    @PostMapping("/getItemFaultStatistics")
    public JsonResult<List<ItemAlarmNumberInfo>> getItemFaultStatistics(@RequestParam("sysCode")String sysCode,
                                                                        @RequestParam("startTime")String startTime,
                                                                        @RequestParam("endTime")String endTime){
        return JsonResult.success(elevatorService.getItemFaultStatistics(sysCode,startTime,endTime));
    }
}
