package com.thtf.environment.controller;

import com.thtf.common.dto.itemserver.ItemTotalAndOnlineAndAlarmNumDTO;
import com.thtf.common.response.JsonResult;
import com.thtf.environment.dto.PageInfoVO;
import com.thtf.environment.service.EnvMonitorService;
import com.thtf.environment.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/25 09:13
 * @Description: 环境监测接口
 */
@RestController
@RequestMapping("/env_monitor")
@Slf4j
public class EnvMonitorController {

    @Autowired
    EnvMonitorService envMonitorService;

    /**
     * @Author: liwencai
     * @Description: 获取前端展示数据
     * @Date: 2022/10/25
     * @Param: sysCode: 子系统编码
     * @Return: com.thtf.common.response.JsonResult<com.thtf.environment.vo.EnvMonitorDisplayVO>
     */
    @PostMapping("/display_info")
    public JsonResult<ItemTotalAndOnlineAndAlarmNumDTO> getDisplayInfo(@RequestParam("sysCode") String sysCode,
                                                                       @RequestParam(value = "areaCode",required = false) String areaCode,
                                                                       @RequestParam(value = "buildingCodes",required = false) String buildingCodes){
        return JsonResult.querySuccess(envMonitorService.getDisplayInfo(sysCode,areaCode,buildingCodes));
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备类别编码和名称
     * @Date: 2022/10/25
     * @Param: sysCode: 子系统编码
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.environment.vo.CodeNameVO>>
     */
    @PostMapping("/item_type")
    public JsonResult<List<CodeNameVO>> getItemTypeList(@RequestParam("sysCode") String sysCode){
        return JsonResult.querySuccess(envMonitorService.getItemTypeList(sysCode));
    }

    /**
     * @Author: liwencai
     * @Description: 未处理报警数据统计
     * @Date: 2022/10/25
     * @Param: startTime: 开始时间
     * @Param: endTime: 结束时间
     * @Param: buildingCodes: 建筑编码集
     * @Param: areaCode: 区域编码
     * @Return: com.thtf.common.response.JsonResult<com.thtf.environment.vo.EChartsVO>
     */
    @PostMapping("/alarm_unhandled_statistics")
    public JsonResult<EChartsVO> getAlarmUnhandledStatistics(@RequestParam(value = "sysCode") String sysCode,
                                                             @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                             @RequestParam(value = "isHandled",required = false) Boolean isHandled,
                                                             @RequestParam(value = "areaCode",required = false) String areaCode,
                                                             @RequestParam(value = "startTime",required = false) String startTime,
                                                             @RequestParam(value = "endTime",required = false) String endTime){
        return JsonResult.querySuccess(envMonitorService.getAlarmUnhandledStatistics(sysCode,buildingCodes,areaCode,isHandled,startTime,endTime));
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备列表信息
     * @Date: 2022/10/27
     * @Param: paramVO:
     * @Return: com.thtf.common.response.JsonResult<com.thtf.environment.vo.EnvMonitorItemResultVO>
     */
    @PostMapping("/item_info")
    public JsonResult<PageInfoVO> listItemInfo(@RequestBody EnvMonitorItemParamVO paramVO){
        return JsonResult.querySuccess(envMonitorService.listItemInfo(paramVO));
    }

    /**
     * @Author: liwencai
     * @Description: 获取分组设备信息
     * @Date: 2022/10/28
     * @Param: sysCode: 子系统编码
     * @Return: com.thtf.common.response.JsonResult<com.thtf.environment.dto.PageInfoVO>
     */
    @PostMapping("/grouped_item_info")
    public JsonResult<PageInfoVO> listGroupedItemAlarmInfo(@RequestParam("sysCode")String sysCode,
                                                           @RequestParam(value = "groupName",required = false)String groupName,
                                                           @RequestParam(value = "areaName",required = false)String areaName,
                                                           @RequestParam(value = "keyword",required = false)String keyword,
                                                           @RequestParam("pageNumber")Integer pageNumber,
                                                           @RequestParam("pageSize")Integer pageSize){
        return JsonResult.querySuccess(envMonitorService.listGroupedItemAlarmInfo(sysCode,groupName,areaName,keyword,pageNumber,pageSize));
    }

//    /**
//     * @Author: liwencai
//     * @Description: 获取设备参数信息
//     * @Date: 2022/10/25
//     * @Param: itemCode:
//     * @Return: com.thtf.common.response.JsonResult<com.thtf.environment.vo.ItemParameterInfoVO>
//     */
//    @GetMapping("/parameter")
//    JsonResult<List<ItemParameterInfoVO>> listParameter(@RequestParam("itemCode") String itemCode){
//        return JsonResult.querySuccess(envMonitorService.listParameter(itemCode));
//    }

    /**
     * @Author: liwencai
     * @Description: 获取小时度历史数据（天）
     * @Date: 2022/10/25
     * @Param: parameterTypeCode: 参数类别编码
     * @Param: date: yyyy-MM-dd 格式的日期
     * @Return: com.thtf.common.response.JsonResult<com.thtf.environment.vo.EChartsVO>
     */
    @PostMapping("/history_moment_hourly")
    public JsonResult<EChartsVO> getHourlyHistoryMoment(@RequestParam("parameterCode") String parameterCode,
                                                        @RequestParam("itemCode") String itemCode,
                                                        @RequestParam("itemTypeCode") String itemTypeCode,
                                                        @RequestParam("date") String date){
        if(StringUtils.isNotBlank(date)){
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date =  now.format(dtf);
        }
        return JsonResult.querySuccess(envMonitorService.getHourlyHistoryMoment(itemCode,itemTypeCode,parameterCode,date));
    }

    /**
     * @Author: liwencai
     * @Description: 获取日度历史数据（月，星期）
     * @Date: 2022/10/25
     * @Param: parameterTypeCode: 参数类别编码
     * @Param: date: yyyy-MM-dd 格式的日期
     * @Return: com.thtf.common.response.JsonResult<com.thtf.environment.vo.EChartsVO>
     */
    @PostMapping("/history_moment_daily")
    public JsonResult<EChartsVO> getDailyHistoryMoment(@RequestParam("parameterCode") String parameterCode,
                                                       @RequestParam("itemCode") String itemCode,
                                                       @RequestParam("itemTypeCode") String itemTypeCode,
                                                       @RequestParam("date") String date){
        return JsonResult.querySuccess(envMonitorService.getDailyHistoryMoment(itemCode,itemTypeCode,parameterCode,date));
    }

    /**
     * @Author: liwencai
     * @Description: 获取月度历史数据（年）
     * @Date: 2022/10/25
     * @Param: parameterTypeCode: 参数类别编码
     * @Param: date: yyyy-MM-dd 格式的日期
     * @Return: com.thtf.common.response.JsonResult<com.thtf.environment.vo.EChartsVO>
     */
    @PostMapping("history_moment_monthly")
    public JsonResult<EChartsVO> getMonthlyHistoryMoment(@RequestParam("parameterCode") String parameterCode,
                                                         @RequestParam("itemCode") String itemCode,
                                                         @RequestParam("itemTypeCode") String itemTypeCode,
                                                         @RequestParam("date") String date){
        return JsonResult.querySuccess(envMonitorService.getMonthlyHistoryMoment(itemCode,itemTypeCode,parameterCode,date));
    }

}
