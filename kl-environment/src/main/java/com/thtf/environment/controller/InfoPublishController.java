package com.thtf.environment.controller;

import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.environment.dto.PageInfoVO;
import com.thtf.environment.service.InfoPublishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liwencai
 * @Date: 2022/9/21 17:19
 * @Description: 信息发布接口
 */
@RestController
@RequestMapping("/info_publish")
@Slf4j
public class InfoPublishController {
    // todo 信息发布需要对接高博医院的信息发布的系统，做对发布信息的统计
    @Autowired
    InfoPublishService infoPublishService;
    @Resource
    ItemAPI itemAPI;
    @Resource
    AlarmAPI alarmAPI;

    /**
     * @Author: liwencai
     * @Description: 获取当日最新报警信息
     * @Date: 2022/9/22
     * @Param sysCode:
     * @Param pageNumber:
     * @Param pageSize:
     * @return: com.thtf.common.response.JsonResult
     */
    @GetMapping("/getAlarmUnhandledToday")
    public JsonResult alarmUnhandledToday(@RequestParam("sysCode") String sysCode,
                                          @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
                                          @RequestParam(value = "pageSize",required = false)Integer pageSize){

        try {
            return alarmAPI.alarmUnhandledToday(sysCode,null,null,pageNumber,pageSize);
        }catch (Exception e){
            log.error(e.getMessage());
            return JsonResult.error("服务器错误");
        }
    }

    /**
     * @Author: liwencai
     * @Description: 获取信息发布大屏数量信息
     * @Date: 2022/9/22
     * @Param sysCode:
     * @Param areaCode:
     * @return: com.thtf.common.response.JsonResult
     */
    @PostMapping("/countInfoPublicItemStatus")
    public JsonResult<Map<String,Integer>> countInfoPublicItemStatus(@RequestParam("sysCode")String sysCode,
                                                                     @RequestParam(value = "areaCodes",required = false)String areaCode,
                                                                     @RequestParam(value = "itemTypeCodes",required = false)String itemTypeCodes){
        return itemAPI.countInfoPublicItemStatus(sysCode,areaCode,itemTypeCodes);
    }

    /**
     * @Author: liwencai
     * @Description: 查询大屏信息
     * @Date: 2022/9/22
     * @Param sysCode: 子系统编码
     * @Param areaCode: 区域编码
     * @Param keyword: 关键字
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.environment.dto.ItemInfoOfLargeScreenDTO>>
     */
    @PostMapping("/getLargeScreenInfo")
    public JsonResult<PageInfoVO> getLargeScreenInfo(@RequestParam("sysCode") String sysCode,
                                                     @RequestParam(value = "areaCode",required = false) String areaCode,
                                                     @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                     @RequestParam(value = "onOffStatus",required = false) Integer onOffStatus,
                                                     @RequestParam(value = "keyword",required = false) String keyword,
                                                     @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
                                                     @RequestParam(value = "pageSize",required = false) Integer pageSize){
        Map<String, Object> map = new HashMap<>();
        map.put("sysCode",sysCode);
        if (StringUtils.isNotBlank(buildingCodes)){
            map.put("buildingCodes", buildingCodes);
        }else {
            if(StringUtils.isNotBlank(areaCode)){
                map.put("areaCode",areaCode);
            }
        }
        if(null != onOffStatus){
            map.put("onOffStatus",onOffStatus);
        }
        if(StringUtils.isNotBlank(keyword)){
            map.put("keyword",keyword);
        }
        if(null != pageNumber){
            map.put("pageNumber",pageNumber);
        }
        if(null != pageSize){
            map.put("pageSize",pageSize);
        }
        return JsonResult.success(infoPublishService.getLargeScreenInfo(map));
    }

    // todo 查询终端发布内容

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/9/22
     * @Param sysCode: 子系统编码
     * @Param keyword: 关键字
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.environment.dto.AlarmInfoOfLargeScreenDTO>>
     */
    @PostMapping("/getLargeScreenAlarmInfo")
    public JsonResult<PageInfoVO> getLargeScreenAlarmInfo(@RequestParam("sysCode") String sysCode,
                                                          @RequestParam(value = "keyword",required = false) String keyword,
                                                          @RequestParam(value = "pageNumber") Integer pageNumber,
                                                          @RequestParam(value = "pageSize") Integer pageSize){
        return JsonResult.success(infoPublishService.getLargeScreenAlarmInfo(sysCode,keyword,pageNumber,pageSize));
    }
}
