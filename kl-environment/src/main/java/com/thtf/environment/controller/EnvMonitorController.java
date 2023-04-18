package com.thtf.environment.controller;

import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.alarmserver.ItemAlarmInfoDTO;
import com.thtf.common.dto.alarmserver.ItemAlarmInfoVO;
import com.thtf.common.dto.itemserver.GroupAlarmInfoVO;
import com.thtf.common.dto.itemserver.ItemTotalAndOnlineAndAlarmNumDTO;
import com.thtf.common.dto.itemserver.ListParameterMapDTO;
import com.thtf.common.response.JsonResult;
import com.thtf.environment.dto.EChartsMoreVO;
import com.thtf.environment.dto.EnvItemMonitorDTO;
import com.thtf.environment.dto.PageInfoVO;
import com.thtf.environment.service.EnvMonitorService;
import com.thtf.environment.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    private EnvMonitorService envMonitorService;

    /**
     * @Author: liwencai
     * @Description: 分堆（前端模型使用）
     * @Date: 2022/12/21
     * @Param listParameterMapDTO:
     * @Return: com.thtf.common.response.JsonResult
     */
    @PostMapping("/listParameterMap")
    public JsonResult listParameterMap(@RequestBody ListParameterMapDTO listParameterMapDTO){
        return JsonResult.querySuccess(envMonitorService.listParameterMap(listParameterMapDTO));
    }

    /**
     * @Author: liwencai
     * @Description: 获取同类别的设备信息
     * @Date: 2022/11/23
     * @Param sysCode: 子系统编码
     * @Param itemTypeCode:
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.environment.vo.ItemCodeAndNameAndTypeVO>>
     */
    @PostMapping("/listItemCodeAndTypeCodeByTypeCode")
    public JsonResult<List<ItemCodeAndNameAndTypeVO>> listItemCodeAndTypeCodeByTypeCode(@RequestParam("sysCode") String sysCode,
                                                        @RequestParam("itemTypeCode") String itemTypeCode){
        return JsonResult.querySuccess(envMonitorService.listItemCodeAndTypeCodeByTypeCode(sysCode,itemTypeCode));
    }

    /**
     * @Author: liwencai
     * @Description: 监测点位信息
     * @Date: 2022/12/5
     * @Param itemCode: 设备编码
     * @Return: com.thtf.common.response.JsonResult
     */
    @PostMapping("/monitor_point_info")
    public JsonResult<EnvItemMonitorDTO> monitor_point_info(@RequestParam("itemCode") String itemCode){
        return JsonResult.querySuccess(envMonitorService.getMonitorPointInfo(itemCode));

    }

    /**
     * @Author: liwencai
     * @Description: 获取前端展示数据
     * @Date: 2022/10/25
     * @Param: sysCode: 子系统编码
     * @Return: com.thtf.common.response.JsonResult<com.thtf.environment.vo.EnvMonitorDisplayVO>
     */
    @PostMapping("/display_group_alarm")
    public JsonResult<List<GroupAlarmInfoVO>> getGroupAlarmDisplayInfo(@RequestParam("sysCode") String sysCode,
                                                                       @RequestParam(value = "areaCode",required = false) String areaCode,
                                                                       @RequestParam(value = "buildingCodes",required = false) String buildingCodes){
        return JsonResult.querySuccess(envMonitorService.getGroupAlarmDisplayInfo(sysCode,areaCode,buildingCodes));
    }

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
     * 获取监测参数单位
     * @author liwencai
     * @param sysCode 子系统编码
     * @param itemTypeCodeList 设备类别编码集
     * @return {@link JsonResult<Map<String,String>>}
     */
    @GetMapping("item_type_parameter_unit")
    public JsonResult<List<CodeUnitVO>> getParameterUnit(@RequestParam("sysCode") String sysCode,
                                                               @RequestParam(value = "itemTypeCodes",required = false) List<String> itemTypeCodeList){
        return JsonResult.querySuccess(envMonitorService.getParameterUnit(sysCode, itemTypeCodeList));
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
     * @Description: 以24小时为维度统计报警总数,每日的每小时累加
     * @Date: 2023/1/11
     * @Param sysCode: 子系统编码
     * @Param buildingCodes: 建筑编码集
     * @Param isHandled: 是否是已处理
     * @Param areaCode: 区域编码
     * @Param startTime: 开始时间
     * @Param endTime: 结束时间
     * @Return: com.thtf.common.response.JsonResult
     */
    @PostMapping("/hourly_total")
    public JsonResult<EChartsMoreVO> getTotalAlarmHourly(@RequestParam(value = "sysCode") String sysCode,
                                                         @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                         @RequestParam(value = "areaCode",required = false) String areaCode,
                                                         @RequestParam(value = "isHandled",required = false) Boolean isHandled,
                                                         @RequestParam(value = "startTime",required = false) String startTime,
                                                         @RequestParam(value = "endTime",required = false) String endTime){
        return JsonResult.querySuccess(envMonitorService.getTotalAlarmHourly(sysCode,buildingCodes,areaCode,isHandled,startTime,endTime));
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备列表信息
     * @Date: 2022/10/27
     * @Param: paramVO:
     * @Return: com.thtf.common.response.JsonResult<com.thtf.environment.vo.EnvMonitorItemResultVO>
     */
    @PostMapping("/item_info")
    public JsonResult<PageInfo<EnvMonitorItemResultVO>> listItemInfo(@RequestBody EnvMonitorItemParamVO paramVO){
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
                                                           @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                           @RequestParam(value = "areaCode",required = false) String areaCode,
                                                           @RequestParam(value = "groupName",required = false)String groupName,
                                                           @RequestParam(value = "areaName",required = false)String areaName,
                                                           @RequestParam(value = "keyword",required = false)String keyword,
                                                           @RequestParam("pageNumber")Integer pageNumber,
                                                           @RequestParam("pageSize")Integer pageSize){
        return JsonResult.querySuccess(envMonitorService.listGroupedItemAlarmInfo(sysCode,buildingCodes,areaCode,groupName,areaName,keyword,pageNumber,pageSize));
    }

    /**
     * @Author: liwencai
     * @Description: 获取小时度历史数据（天）
     * @Date: 2022/10/25
     * @Param parameterCode: 设备参数编码
     * @Param itemCode: 设备编码
     * @Param itemTypeCode: 设备类别编码
     * @Param date: 日期
     * @Return: com.thtf.common.response.JsonResult<com.thtf.environment.vo.EChartsVO>
     */
    @PostMapping("/history_moment_hourly")
    public JsonResult<EChartsVO> getHourlyHistoryMoment(@RequestParam(value = "parameterCode",required = false) String parameterCode,
                                                        @RequestParam(value = "itemCode",required = false) String itemCode,
                                                        @RequestParam(value = "itemTypeCode",required = false) String itemTypeCode,
                                                        @RequestParam("date") String date){
        if(StringUtils.isBlank(date)){
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
    public JsonResult<EChartsVO> getDailyHistoryMoment(@RequestParam(value = "parameterCode",required = false) String parameterCode,
                                                       @RequestParam(value = "itemCode",required = false) String itemCode,
                                                       @RequestParam(value = "itemTypeCode",required = false) String itemTypeCode,
                                                       @RequestParam("date") String date) throws Exception {
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
    @PostMapping("/history_moment_monthly")
    public JsonResult<EChartsVO> getMonthlyHistoryMoment(@RequestParam(value = "parameterCode",required = false) String parameterCode,
                                                         @RequestParam(value = "itemCode",required = false) String itemCode,
                                                         @RequestParam(value = "itemTypeCode",required = false) String itemTypeCode,
                                                         @RequestParam("date") String date){
        return JsonResult.querySuccess(envMonitorService.getMonthlyHistoryMoment(itemCode,itemTypeCode,parameterCode,date));
    }

    /**
     * @Author: liwencai
     * @Description: 获取指定区域不同设备的报警情况（如感烟探测器、温感探测器、手动火灾报警按钮）
     * @Date: 2022/8/16
     * @Param param:
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.common.dto.alarmserver.ItemAlarmInfoDTO>>
     */
    @PostMapping("/alarm_items_area")
    public JsonResult<List<ItemAlarmInfoDTO>> getItemsAlarmInfo(@RequestParam("sysCode")String sysCode,
                                                                @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                                @RequestParam(value = "areaCode",required = false) String areaCode,
                                                                @RequestParam(value = "itemTypeCodes",required = false) String itemTypeCodes){
        ItemAlarmInfoVO param = new ItemAlarmInfoVO();
        if(StringUtils.isNotBlank(itemTypeCodes)){
            param.setItemTypeCodeList(Arrays.asList(itemTypeCodes.split(",")));
        }
        param.setSysCode(sysCode);
        param.setBuildingCodes(buildingCodes);
        param.setAreaCode(areaCode);
        return envMonitorService.getItemsAlarmInfo(param);
    }
}
