package com.thtf.environment.controller;

import com.github.pagehelper.PageInfo;
import com.thtf.common.response.JsonResult;
import com.thtf.environment.dto.AlarmInfoOfBroadcastDTO;
import com.thtf.environment.dto.BroadcastContentInsertDTO;
import com.thtf.environment.dto.DisplayInfoDTO;
import com.thtf.environment.dto.ItemInfoOfBroadcastDTO;
import com.thtf.environment.service.BroadcastService;
import com.thtf.environment.service.InfoPublishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/7 13:43
 * @Description: 广播接口
 */
@RestController
@RequestMapping("/broadcast")
@Slf4j
public class BroadcastController {

    @Resource
    private BroadcastService broadcastService;

    @Resource
    private InfoPublishService infoPublishService;

    /**
     * @Author: liwencai
     * @Description: 前端展示界面数据
     * @Date: 2022/10/7
     * @Param sysCode: 子系统编码
     * @Param itemType: 设备类别
     * @return: com.thtf.common.response.JsonResult<java.util.List < com.thtf.broadcast.dto.DisplayInfoDTO>>
     */
    @PostMapping("/displayInfo")
    JsonResult<DisplayInfoDTO> displayInfo(@RequestParam(value = "sysCode") String sysCode,
                                           @RequestParam(value = "buildingCodes", required = false) String buildingCodes,
                                           @RequestParam(value = "areaCode", required = false) String areaCode) {
        return JsonResult.querySuccess(broadcastService.displayInfo(sysCode, buildingCodes, areaCode));
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/12/1
     * @Param sysCode: 子系统编码
     * @Param itemCodes: 设备编码集
     * @return: com.thtf.common.response.JsonResult
     */
    @GetMapping("/monitor_point_info")
    public JsonResult<ItemInfoOfBroadcastDTO> getMonitorPoint(@RequestParam("sysCode") String sysCode,
                                                              @RequestParam("itemCode") String itemCodes) {
        return JsonResult.querySuccess(broadcastService.getMonitorPoint(sysCode, itemCodes));

    }

    /**
     * @Author: liwencai
     * @Description: 获取广播设备信息
     * @Date: 2022/10/7
     * @Param keyword: 关键词
     * @Param sysCode: 子系统编码
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: com.thtf.common.response.JsonResult<com.github.pagehelper.PageInfo < com.thtf.environment.dto.ItemInfoOfBroadcastDTO>>
     */
    @PostMapping("/getItemInfo")
    JsonResult<PageInfo<ItemInfoOfBroadcastDTO>> getItemInfo(@RequestParam("sysCode") String sysCode,
                                                             @RequestParam(value = "buildingCodes", required = false) String buildingCodes,
                                                             @RequestParam(value = "areaCode", required = false) String areaCode,
                                                             @RequestParam(value = "onlineValue", required = false) String onlineValue,
                                                             @RequestParam(value = "keyword", required = false) String keyword,
                                                             @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                             @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return JsonResult.querySuccess(broadcastService.getItemInfo(sysCode, buildingCodes, areaCode, onlineValue, keyword, pageNumber, pageSize));
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
    JsonResult<PageInfo<AlarmInfoOfBroadcastDTO>> getAlarmInfo(@RequestParam(value = "sysCode") String sysCode,
                                                               @RequestParam(value = "areaCode", required = false) String areaCode,
                                                               @RequestParam(value = "buildingCodes", required = false) String buildingCodes,
                                                               @RequestParam(value = "keyword", required = false) String keyword,
                                                               @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                               @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return JsonResult.querySuccess(broadcastService.getAlarmInfo(keyword, sysCode, buildingCodes, areaCode, pageNumber, pageSize));
    }

    /**
     * @Author: liwencai
     * @Description: 终端监听 获取终端内容
     * @Date: 2022/11/3
     * @Param: itemCode:
     * @Return: com.thtf.common.response.JsonResult<java.util.List < com.thtf.environment.dto.BroadcastPublishContentDTO>>
     */
    @GetMapping("/getPublishContent")
    JsonResult<List<BroadcastContentInsertDTO>> getPublishContent(@RequestParam("itemCode") String itemCode) {
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
    JsonResult<Boolean> publishContent(@RequestBody BroadcastContentInsertDTO param) {
        Boolean flag = broadcastService.publishContent(param);
        if (flag) {
            return JsonResult.success();
        } else {
            return JsonResult.error("发布失败");
        }
    }

    /**
     * @Author: liwencai
     * @Description: 切换远程开关的状态
     * @Date: 2022/11/2
     * @Param: sysCode: 子系统编码
     * @Param: itemCodes: 设备编码集
     * @Return: com.thtf.common.response.JsonResult<java.lang.Boolean>
     */
    @PostMapping("/remote_switch")
    public JsonResult<Boolean> remoteSwitch(@RequestParam("sysCode") String sysCode,
                                            @RequestParam("itemCodes") String itemCodes) {
        if (StringUtils.isNotBlank(itemCodes)) {
            Boolean aBoolean = infoPublishService.remoteSwitch(sysCode, itemCodes);
            if (aBoolean) {
                return JsonResult.success();
            } else {
                return JsonResult.error("修改失败");
            }
        } else {
            return JsonResult.error("请传入设备编码");
        }
    }
}
