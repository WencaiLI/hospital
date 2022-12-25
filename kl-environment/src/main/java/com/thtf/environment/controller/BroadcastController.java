package com.thtf.environment.controller;


import com.thtf.common.entity.adminserver.TblBuildingArea;
import com.thtf.common.entity.alarmserver.TblAlarmRecord;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.environment.common.Constant.ParameterConstant;
import com.thtf.environment.dto.*;
import com.thtf.environment.service.BroadcastService;
import com.thtf.environment.service.InfoPublishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private BroadcastService broadcastService;

    @Autowired
    private InfoPublishService infoPublishService;

    @Resource
    private AlarmAPI alarmAPI;

    @Resource
    private AdminAPI adminAPI;


//    /**
//     * @Author: liwencai
//     * @Description: 获取楼层信息
//     * @Date: 2022/9/22
//     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.common.entity.adminserver.TblBuildingArea>>
//     */
//    @GetMapping("/getFloorInfo")
//    public JsonResult<List<TblBuildingArea>> getFloorInfo(@RequestParam(value = "buildingCode",required = false) String buildingCode,
//                                                          @RequestParam(value = "sysCode",required = false) String systemCode){
//        try {
//            return adminAPI.getFloorInfo(buildingCode,systemCode);
//        }catch (Exception e){
//            log.error(e.getMessage());
//            return JsonResult.error("服务器错误");
//        }
//    }

//    /**
//     * @Author: liwencai
//     * @Description: 获取当前最新报警（当日）
//     * @Date: 2022/10/7
//     * @Param sysCode:
//     * @Param pageNumber:
//     * @Param pageSize:
//     * @return: com.thtf.common.response.JsonResult
//     */
//    @GetMapping(value = "/getAlarmUnhandledToday")
//    public JsonResult alarmUnhandledToday(@RequestParam("sysCode") String sysCode,
//                                          @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
//                                          @RequestParam(value = "pageSize",required = false) Integer pageSize){
//
//        try {
//            return alarmAPI.alarmUnhandledToday(sysCode,null,null,pageNumber,pageSize);
//        }catch (Exception e){
//            log.error(e.getMessage());
//            return JsonResult.error("服务器错误");
//        }
//    }
//
//    /**
//     * @Author: liwencai
//     * @Description: 报警处置
//     * @Date: 2022/9/2
//     * @Param param:
//     * @return: com.thtf.common.response.JsonResult<java.lang.Boolean>
//     */
//    @PutMapping(value = "/disposalAlarm")
//    public JsonResult<Boolean> alarmDisposal(@RequestBody TblAlarmRecord param){
//        try {
//            alarmAPI.handleAlarm(param);
//            return JsonResult.success();
//        }catch (Exception e){
//            return JsonResult.error("操作失败");
//        }
//    }

    /**
     * @Author: liwencai
     * @Description: 前端展示界面数据
     * @Date: 2022/10/7
     * @Param sysCode: 子系统编码
     * @Param itemType: 设备类别
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.broadcast.dto.DisplayInfoDTO>>
     */
    @PostMapping("/displayInfo")
    JsonResult<DisplayInfoDTO> displayInfo(@RequestParam(value = "sysCode")String sysCode,
                                           @RequestParam(value = "buildingCodes",required = false)String buildingCodes,
                                           @RequestParam(value = "areaCode",required = false) String areaCode){
        return JsonResult.querySuccess(broadcastService.displayInfo(sysCode,buildingCodes,areaCode));
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
        return JsonResult.querySuccess(broadcastService.controlInfo(sysCode));
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/12/1
     * @Param sysCode:
     * @Param itemCodes:
     * @return: com.thtf.common.response.JsonResult
     */
    @GetMapping("/monitor_point_info")
    public JsonResult<ItemInfoOfBroadcastDTO> getMonitorPoint(@RequestParam("sysCode") String sysCode,
                                                                @RequestParam("itemCode") String itemCodes){
        return JsonResult.querySuccess(broadcastService.getMonitorPoint(sysCode,itemCodes));

    }


    /**
     * @Author: liwencai
     * @Description: 获取广播设备信息
     * @Date: 2022/10/7
     * @Param keyword: 关键词
     * @Param sysCode: 子系统编码
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: com.thtf.common.response.JsonResult<com.github.pagehelper.PageInfo<com.thtf.environment.dto.ItemInfoOfBroadcastDTO>>
     */
    @PostMapping("/getItemInfo")
    JsonResult<PageInfoVO> getItemInfo(@RequestParam("sysCode") String sysCode,
                                       @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                       @RequestParam(value = "areaCode",required = false) String areaCode,
                                       @RequestParam(value = "runValue",required = false) String runValue,
                                       @RequestParam(value = "keyword",required = false) String keyword,
                                       @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
                                       @RequestParam(value = "pageSize",required = false) Integer pageSize){
        return JsonResult.querySuccess(broadcastService.getItemInfo(sysCode,buildingCodes,areaCode,runValue,keyword,pageNumber,pageSize));
    }

    /**
     * @Author: liwencai
     * @Description: 获取报警信息
     * @Date: 2022/10/7
     * @Param keyword: 关键词
     * @Param sysCode: 子系统编码
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: com.thtf.common.response.JsonResult<com.thtf.environment.dto.PageInfoVO>
     */
    @PostMapping("/getAlarmInfo")
    JsonResult<PageInfoVO> getAlarmInfo(@RequestParam(value = "sysCode") String sysCode,
                                        @RequestParam(value = "keyword",required = false) String keyword,
                                        @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
                                        @RequestParam(value = "pageSize",required = false) Integer pageSize){
        return JsonResult.querySuccess(broadcastService.getAlarmInfo(keyword,sysCode,pageNumber,pageSize));
    }

    /**
     * @Author: liwencai
     * @Description: 终端监听 获取终端内容
     * @Date: 2022/11/3
     * @Param: itemCode:
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.environment.dto.BroadcastPublishContentDTO>>
     */
    @GetMapping("/getPublishContent")
    JsonResult<List<BroadcastContentInsertDTO>>  getPublishContent(@RequestParam("itemCode") String itemCode){
        return JsonResult.querySuccess(broadcastService.getPublishContent(itemCode));
    }

    /**
     * @Author: liwencai
     * @Description: 
     * @Date: 2022/11/3
     * @Param: param: 
     * @Return: com.thtf.common.response.JsonResult<java.lang.Boolean>
     */
    @PostMapping("/publishContent")
    JsonResult<Boolean>  publishContent(@RequestBody BroadcastContentInsertDTO param){
        Boolean flag = broadcastService.publishContent(param);
        if(flag){
            return JsonResult.success();
        }else {
            return JsonResult.error("发布失败");
        }
    }

    /**
     * @Author: liwencai
     * @Description: 切换远程开关的状态
     * @Date: 2022/11/2
     * @Param: sysCode:
     * @Param: itemCodes:
     * @Return: com.thtf.common.response.JsonResult<java.lang.Boolean>
     */
    @PostMapping("/remote_switch")
    public JsonResult<Boolean> remoteSwitch(@RequestParam("sysCode") String sysCode,
                                            @RequestParam("itemCodes") String itemCodes){
        if(StringUtils.isNotBlank(itemCodes)){
            Boolean aBoolean = infoPublishService.remoteSwitch(sysCode, itemCodes);
            if(aBoolean){
                return JsonResult.success();
            }else {
                return JsonResult.error("修改失败");
            }
        }else {
            return JsonResult.error("请传入设备编码");
        }
    }

//    /**
//     * @Author: liwencai
//     * @Description: 分组的相关数据
//     * @Date: 2022/11/4
//     * @Param: sysCode: 子系统编码
//     * @Return: com.thtf.common.response.JsonResult
//     */
//    @PostMapping("/group_count")
//    public JsonResult countGroup(@RequestParam("sysCode") String sysCode){
//        return itemAPI.countGroupByParameter(sysCode, ParameterConstant.GB_TASK,ParameterConstant.GB_TASK_ON_VALUE);
//    }
}
