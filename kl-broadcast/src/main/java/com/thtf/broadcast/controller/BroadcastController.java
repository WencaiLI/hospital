package com.thtf.broadcast.controller;

import com.thtf.broadcast.dto.DisplayInfoDTO;
import com.thtf.broadcast.service.BroadcastService;
import com.thtf.common.entity.adminserver.TblBuildingArea;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.response.JsonResult;
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
 * @Description:
 */
@RestController
@RequestMapping("/broadcast")
@Slf4j
public class BroadcastController {

    @Autowired
    private BroadcastService broadcastService;

    @Resource
    private AlarmAPI alarmAPI;

    @Resource
    private AdminAPI adminAPI;

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
     * @Description: 前端展示界面数据
     * @Date: 2022/10/7
     * @Param sysCode:
     * @Param itemType:
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.broadcast.dto.DisplayInfoDTO>>
     */
    @PostMapping("/displayInfo")
    JsonResult<List<DisplayInfoDTO>> displayInfo(@RequestParam(value ="sysCode")String sysCode,
                                                 @RequestParam(value ="itemType")String itemType){
        return JsonResult.success(broadcastService.displayInfo(sysCode,itemType));
    }


}
