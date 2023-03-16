package com.thtf.environment.controller.app;

import com.thtf.common.response.JsonResult;
import com.thtf.environment.dto.AppEnvMonitorDisplayDTO;
import com.thtf.environment.dto.AppListAlarmParamDTO;
import com.thtf.environment.dto.KeyValueDTO;
import com.thtf.environment.service.AppEnvMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/12/15 21:04
 * @Description:
 */
@RestController
@RequestMapping("/env_monitor/app")
@Slf4j
public class AppEnvMonitorController {

    @Autowired
    AppEnvMonitorService appEnvMonitorService;

    @PostMapping("/display_info")
    public JsonResult<AppEnvMonitorDisplayDTO> getDisplayInfo(@RequestParam("sysCode") String sysCode,
                                                              @RequestParam(value = "buildingCodes",required = false) String buildingCodes){
        return JsonResult.querySuccess(appEnvMonitorService.getDisplayInfo(sysCode,buildingCodes));
    }


    @PostMapping("/alarm_count")
    public JsonResult<List<KeyValueDTO>> getAlarmCount(@RequestParam("sysCode") String sysCode,
                                                       @RequestParam(value = "buildingCodes",required = false) String buildingCodes){
        return JsonResult.querySuccess(appEnvMonitorService.getAlarmCount(sysCode,buildingCodes));
    }

    @PostMapping("/listAlarmUnhandled")
    public JsonResult listAlarmUnhandled(@RequestBody AppListAlarmParamDTO paramDTO){
        return JsonResult.querySuccess(appEnvMonitorService.listAlarmUnhandled(paramDTO));
    }

    @PostMapping("/listGroupInfo")
    public JsonResult<List<KeyValueDTO>> listGroupInfo(@RequestParam("sysCode") String sysCode,
                                                       @RequestParam("buildingCodes") String buildingCodes){
        return JsonResult.querySuccess(appEnvMonitorService.listGroupInfo(sysCode,buildingCodes));
    }

    @PostMapping("/listTypeInfo")
    public JsonResult<List<KeyValueDTO>> listTypeInfo(@RequestParam("sysCode") String sysCode){
        return JsonResult.querySuccess(appEnvMonitorService.listTypeInfo(sysCode));
    }
    // todo liwencai 缺少以根据建筑编码集，查询分组的接口
}
