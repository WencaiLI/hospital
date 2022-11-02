package com.thtf.environment.controller;


import com.thtf.common.entity.adminserver.TblBuildingArea;
import com.thtf.common.entity.alarmserver.TblAlarmRecord;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.environment.dto.DisplayInfoDTO;
import com.thtf.environment.dto.KeyValueDTO;
import com.thtf.environment.dto.PageInfoVO;
import com.thtf.environment.service.BroadcastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: liwencai
 * @Date: 2022/10/7 13:43
 * @Description: 广播接口
 */
@RestController
@RequestMapping("/broadcast")
@Slf4j
public class BroadcastController {

    @Autowired
    BroadcastService broadcastService;

    @Resource
    AlarmAPI alarmAPI;

    @Resource
    AdminAPI adminAPI;

    /**
     * @Author: liwencai
     * @Description: 获取楼层信息
     * @Date: 2022/9/22
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.common.entity.adminserver.TblBuildingArea>>
     */
    @GetMapping("/getFloorInfo")
    public JsonResult<List<TblBuildingArea>> getFloorInfo(@RequestParam(value = "buildingCode",required = false) String buildingCode){
        try {
            return adminAPI.getFloorInfo(buildingCode);
        }catch (Exception e){
            log.error(e.getMessage());
            return JsonResult.error("服务器错误");
        }
    }

    /**
     * @Author: liwencai
     * @Description: 获取当前最新报警（当日）
     * @Date: 2022/10/7
     * @Param sysCode:
     * @Param pageNumber:
     * @Param pageSize:
     * @return: com.thtf.common.response.JsonResult
     */
    @GetMapping(value = "/getAlarmUnhandledToday")
    public JsonResult alarmUnhandledToday(@RequestParam("sysCode") String sysCode,
                                          @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
                                          @RequestParam(value = "pageSize",required = false) Integer pageSize){

        try {
            return alarmAPI.alarmUnhandledToday(sysCode,null,null,pageNumber,pageSize);
        }catch (Exception e){
            log.error(e.getMessage());
            return JsonResult.error("服务器错误");
        }
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
     * @Description: 前端展示界面数据
     * @Date: 2022/10/7
     * @Param sysCode:
     * @Param itemType:
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.broadcast.dto.DisplayInfoDTO>>
     */
    @PostMapping("/displayInfo")
    JsonResult<DisplayInfoDTO> displayInfo(@RequestParam(value = "sysCode")String sysCode,
                                                 @RequestParam(value = "buildingCodes",required = false)String buildingCodes,
                                                 @RequestParam(value = "areaCode") String areaCode){
        return JsonResult.success(broadcastService.displayInfo(sysCode,buildingCodes,areaCode));
    }

    /**
     * @Author: liwencai
     * @Description: 控制信息
     * @Date: 2022/10/7
     * @Param sysCode: 子系统编码
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.environment.dto.KeyValueDTO>>
     */
    @PostMapping("/controlInfo")
    JsonResult<List<KeyValueDTO>> controlInfo(@RequestParam(value ="sysCode")String sysCode){
        return JsonResult.success(broadcastService.controlInfo(sysCode));
    }

    /**
     * @Author: liwencai
     * @Description: 获取广播设备信息
     * @Date: 2022/10/7
     * @Param keyword:
     * @Param sysCode:
     * @Param pageNumber:
     * @Param pageSize:
     * @return: com.thtf.common.response.JsonResult<com.github.pagehelper.PageInfo<com.thtf.environment.dto.ItemInfoOfBroadcastDTO>>
     */
    @PostMapping("/getItemInfo")
    JsonResult<PageInfoVO> getItemInfo(@RequestParam(value = "keyword") String keyword,
                                       @RequestParam(value = "sysCode") String sysCode,
                                       @RequestParam(value = "areaCode",required = false) String areaCode,
                                       @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
                                       @RequestParam(value = "pageSize",required = false) Integer pageSize){
        return JsonResult.success(broadcastService.getItemInfo(keyword,sysCode,areaCode,pageNumber,pageSize));
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/10/7
     * @Param keyword:
     * @Param sysCode:
     * @Param pageNumber:
     * @Param pageSize:
     * @return: com.thtf.common.response.JsonResult<com.thtf.environment.dto.PageInfoVO>
     */
    @PostMapping("/getAlarmInfo")
    JsonResult<PageInfoVO> getAlarmInfo(@RequestParam("keyword") String keyword,
                                        @RequestParam(value = "sysCode") String sysCode,
                                        @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
                                        @RequestParam(value = "pageSize",required = false) Integer pageSize){
        return JsonResult.success(broadcastService.getAlarmInfo(keyword,sysCode,pageNumber,pageSize));
    }

    // todo 终端监听

    // todo 远程控制
}
