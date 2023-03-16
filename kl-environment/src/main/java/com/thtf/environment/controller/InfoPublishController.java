package com.thtf.environment.controller;

import com.github.pagehelper.PageInfo;
import com.thtf.common.entity.itemserver.TblVideoItem;
import com.thtf.common.feign.ItemAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.environment.dto.InfoPublishDisplayDTO;
import com.thtf.environment.dto.ItemInfoOfLargeScreenDTO;
import com.thtf.environment.dto.ItemPlayInfoDTO;
import com.thtf.environment.dto.PageInfoVO;
import com.thtf.environment.service.InfoPublishService;
import com.thtf.environment.vo.ListLargeScreenInfoParamVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/21 17:19
 * @Description: 信息发布接口
 */
@RestController
@RequestMapping("/info_publish")
@Slf4j
public class InfoPublishController {

    @Autowired
    private InfoPublishService infoPublishService;

    @Resource
    private ItemAPI itemAPI;

    /**
     * @Author: liwencai
     * @Description: 获取信息发布大屏数量信息
     * @Date: 2022/9/22
     * @Param sysCode: 子系统编码
     * @Param areaCode: 区域编码
     * @return: com.thtf.common.response.JsonResult
     */
    @PostMapping("/countInfoPublicItemStatus")
    public JsonResult<InfoPublishDisplayDTO> countInfoPublicItemStatus(@RequestParam("sysCode")String sysCode,
                                                                       @RequestParam(value = "buildingCodes",required = false)String buildingCodes,
                                                                       @RequestParam(value = "areaCode",required = false)String areaCode,
                                                                       @RequestParam(value = "itemTypeCodes",required = false)String itemTypeCodes){
        return JsonResult.querySuccess(infoPublishService.getDisplayInfo(sysCode,buildingCodes,areaCode,itemTypeCodes));
    }


    /**
     * @Author: liwencai
     * @Description: 点位信息
     * @Date: 2022/12/1
     * @Param sysCode: 子系统编码
     * @Param itemCode: 设备编码
     * @return: com.thtf.common.response.JsonResult
     */
    @GetMapping("/monitor_point_info")
    public JsonResult<ItemInfoOfLargeScreenDTO> getMonitorPoint(@RequestParam("sysCode") String sysCode,
                                                                @RequestParam("itemCode") String itemCode){
        return JsonResult.querySuccess(infoPublishService.getMonitorPoint(sysCode,itemCode));

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
    public JsonResult<PageInfo<ItemInfoOfLargeScreenDTO>> getLargeScreenInfo(@RequestParam("sysCode") String sysCode,
                                                                             @RequestParam(value = "areaCode",required = false) String areaCode,
                                                                             @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                                             @RequestParam(value = "onlineValue",required = false) String onlineValue,
                                                                             @RequestParam(value = "keyword",required = false) String keyword,
                                                                             @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
                                                                             @RequestParam(value = "pageSize",required = false) Integer pageSize) throws Exception {
        if(null == pageNumber && null == pageSize){
            throw new Exception("分页信息不能为空！");
        }
        ListLargeScreenInfoParamVO paramVO = ListLargeScreenInfoParamVO
                .builder()
                .sysCode(sysCode)
                .buildingCodes(buildingCodes)
                .areaCodes(areaCode)
                .onlineValue(onlineValue)
                .keyword(keyword)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build();
        return JsonResult.querySuccess(infoPublishService.getLargeScreenInfo(paramVO));
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备关联的摄像头信息
     * @Date: 2022/11/2
     * @Param: itemCode: 设备编码
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.common.entity.itemserver.TblVideoItem>>
     */
    @GetMapping("/listRelatedVideo")
    public JsonResult<List<TblVideoItem>> listRelatedVideo(@RequestParam("itemCode")String itemCode){
        return JsonResult.querySuccess(itemAPI.getVideoItemListByItemCode(itemCode).getBody().getData());
    }

    /**
     * @Author: liwencai
     * @Description: 获取信息发布大屏内容
     * @Date: 2022/11/2
     * @Param: sysCode: 子系统编码
     * @Param: buildingCodes: 建筑编码集
     * @Param: areaCode: 区域编码
     * @Param: itemCodes: 设备编码集
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.environment.dto.ItemPlayInfoDTO>>
     */
    @PostMapping("/listLargeScreenContent")
    public JsonResult<List<ItemPlayInfoDTO>> listLargeScreenContent(@RequestParam("sysCode")String  sysCode,
                                                                    @RequestParam(value = "buildingCodes",required = false)String buildingCodes,
                                                                    @RequestParam(value = "areaCode",required = false)String  areaCode,
                                                                    @RequestParam(value = "itemCodes",required = false)String itemCodes){
        return JsonResult.querySuccess(infoPublishService.listLargeScreenContent(sysCode,buildingCodes,areaCode,itemCodes));
    }

    /**
     * @Author: liwencai
     * @Description: 故障信息
     * @Date: 2022/9/22
     * @Param sysCode: 子系统编码
     * @Param keyword: 关键字
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.environment.dto.AlarmInfoOfLargeScreenDTO>>
     */
    @PostMapping("/getLargeScreenAlarmInfo")
    public JsonResult<PageInfoVO> getLargeScreenAlarmInfo(@RequestParam("sysCode") String sysCode,
                                                          @RequestParam(value = "buildingCodes",required = false) String buildingCodes,
                                                          @RequestParam(value = "areaCode",required = false) String areaCode,
                                                          @RequestParam(value = "keyword",required = false) String keyword,
                                                          @RequestParam(value = "pageNumber") Integer pageNumber,
                                                          @RequestParam(value = "pageSize") Integer pageSize){
        return JsonResult.querySuccess(infoPublishService.getLargeScreenAlarmInfo(sysCode,buildingCodes,areaCode,keyword,pageNumber,pageSize));
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

    /**
     * @Author: liwencai
     * @Description: 新增播单
     * @Date: 2023/3/15
     * @Param param:
     * @Return: com.thtf.common.response.JsonResult<java.lang.Boolean>
     */
    @PostMapping("/play_order_insert")
    public JsonResult<Boolean> insertPlayOrder(@RequestBody ItemPlayInfoDTO param){
        return JsonResult.success(infoPublishService.insertPlayOrder(param));
    }
}
