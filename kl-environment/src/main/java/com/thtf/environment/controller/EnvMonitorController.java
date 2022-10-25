package com.thtf.environment.controller;

import com.thtf.common.response.JsonResult;
import com.thtf.environment.service.EnvMonitorService;
import com.thtf.environment.vo.CodeNameVO;
import com.thtf.environment.vo.EChartsVO;
import com.thtf.environment.vo.EnvMonitorDisplayVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public JsonResult<EnvMonitorDisplayVO> display_info(@RequestParam("sysCode") String sysCode){
        return JsonResult.querySuccess(envMonitorService.getDisplayInfo(sysCode));
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备类别编码和名称
     * @Date: 2022/10/25
     * @Param: sysCode:
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
    public JsonResult<EChartsVO> getAlarmUnhandledStatistics(@RequestParam("startTime") String startTime,
                                                             @RequestParam("endTime") String endTime,
                                                             @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                             @RequestParam(value = "areaCode",required = false) String areaCode){
        return JsonResult.querySuccess(envMonitorService.getAlarmUnhandledStatistics(buildingCodes,areaCode,startTime,endTime));
    }

}
