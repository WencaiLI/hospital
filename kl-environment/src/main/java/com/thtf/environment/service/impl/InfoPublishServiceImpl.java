package com.thtf.environment.service.impl;

import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.itemserver.ItemNestedParameterVO;
import com.thtf.common.entity.alarmserver.TblAlarmRecord;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.environment.dto.AlarmInfoOfLargeScreenDTO;
import com.thtf.environment.dto.ItemInfoOfLargeScreenDTO;
import com.thtf.environment.dto.PageInfoVO;
import com.thtf.environment.dto.convert.AlarmConvert;
import com.thtf.environment.dto.convert.ItemConvert;
import com.thtf.environment.dto.convert.PageInfoConvert;
import com.thtf.environment.service.InfoPublishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: liwencai
 * @Date: 2022/9/21 17:23
 * @Description:
 */
@Service
@Slf4j
public class InfoPublishServiceImpl implements InfoPublishService {

    private final static String BIG_SCREEN_TYPE_CODE = "XXFBDP_TYPE"; // 信息发布大屏类别在数据库中的字段

    @Resource
    private ItemAPI itemAPI;
    @Resource
    private AlarmAPI alarmAPI;
    @Resource
    private AdminAPI adminAPI;
    @Resource
    PageInfoConvert pageInfoConvert;
    @Resource
    AlarmConvert alarmConvert;
    @Resource
    RedisOperationService redisOperationService;

    /**
     * @Author: liwencai
     * @Description: 获取大屏信息
     * @Date: 2022/9/23
     * @Param sysCode: 子系统编码
     * @Param areaCode: 区域编码
     * @Param keyword: 关键词
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: java.util.List<com.thtf.environment.dto.ItemInfoOfLargeScreenDTO>
     */
    @Override
    public PageInfoVO getLargeScreenInfo(Map<String, Object> paramMap) {
        PageInfo<ItemNestedParameterVO> itemPageInfo = itemAPI.listItemNestedParametersBySysCodeAndItemCodeListAndParameterKeyAndValueAndKeywordPage(
                (String) paramMap.get("sysCode"), null,null,(String) paramMap.get("areaCode"),
                "OnOffStatus", String.valueOf(1), (String) paramMap.get("keyword"),
                (Integer) paramMap.get("pageNumber"), (Integer) paramMap.get("pageSize")
        ).getData();

        PageInfoVO pageInfoVO;
        pageInfoVO = pageInfoConvert.toPageInfoVO(itemPageInfo);
        List<ItemNestedParameterVO> list = itemPageInfo.getList();

        // 获取设备报警信息
        List<TblAlarmRecordUnhandle> allAlarmRecordUnhandled = alarmAPI.getAlarmInfoByItemCodeListLimitOne(list.stream().map(ItemNestedParameterVO::getCode).collect(Collectors.toList())).getData();

        List<ItemInfoOfLargeScreenDTO> resultList = new ArrayList<>();

        for (ItemNestedParameterVO itemNestedParameterVO : list) {
            ItemInfoOfLargeScreenDTO inner_result = new ItemInfoOfLargeScreenDTO();
            inner_result.setItemId(itemNestedParameterVO.getId());
            inner_result.setItemCode(itemNestedParameterVO.getCode());
            inner_result.setItemName(itemNestedParameterVO.getName());

            if(StringUtils.isNotBlank(itemNestedParameterVO.getViewLongitude())){
                inner_result.setEye(Arrays.stream(itemNestedParameterVO.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }

            if(StringUtils.isNotBlank(itemNestedParameterVO.getViewLatitude())){
                inner_result.setCenter(Arrays.stream(itemNestedParameterVO.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }

            allAlarmRecordUnhandled.forEach(e->{
                if(e.getItemCode().equals(itemNestedParameterVO.getCode())){
                    inner_result.setAlarmStatus(e.getAlarmCategory() == 1?"故障报警":"监测报警");
                }
            });

            for (TblItemParameter p : itemNestedParameterVO.getParameterList()) {
                if (p.getParameterType().equals("OnOffStatus")) {
                    inner_result.setRunParameterCode(p.getCode());
                    inner_result.setRunValue(p.getValue() + (p.getUnit() == null ? "" : p.getUnit()));
                }
                if (p.getParameterType().equals("OnlineStatus")) {
                    inner_result.setOnlineParameterCode(p.getCode());
                    inner_result.setOnlineValue(p.getValue() + (p.getUnit() == null ? "" : p.getUnit()));
                }
                if (p.getParameterType().equals("Capacity")) {
                    inner_result.setCapacityParameterCode(p.getCode());
                    inner_result.setCapacityValue(p.getValue() + (p.getUnit() == null ? "" : p.getUnit()));
                }
                if (p.getParameterType().equals("Luminance")) {
                    inner_result.setLuminanceParameterCode(p.getCode());
                    inner_result.setLuminanceValue(p.getValue() + (p.getUnit() == null ? "" : p.getUnit()));
                }
                if (p.getParameterType().equals("Volume")) {
                    inner_result.setVolumeParameterCode(p.getCode());
                    inner_result.setVolumeValue(p.getValue() + (p.getUnit() == null ? "" : p.getUnit()));
                }
                if (p.getParameterType().equals("StorageStatus")) {
                    inner_result.setStorageStatusParameterCode(p.getCode());
                    inner_result.setStorageStatusValue(p.getValue() + (p.getUnit() == null ? "" : p.getUnit()));
                }
            }
            resultList.add(inner_result);
        }
        pageInfoVO.setList(resultList);
        return pageInfoVO;
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/9/23
     * @Param sysCode:
     * @Param keyword:
     * @Param pageNumber:
     * @Param pageSize:
     * @return: java.util.List<com.thtf.environment.dto.AlarmInfoOfLargeScreenDTO>
     */
    @Override
    public PageInfoVO getLargeScreenAlarmInfo(String sysCode, String keyword, Integer pageNumber, Integer pageSize) {
        PageInfo<TblAlarmRecordUnhandle> data = alarmAPI.getAlarmInfoBySysCodeLimitOneByKeywordPage(keyword, sysCode, pageNumber, pageSize).getData();
        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(data);
        List<AlarmInfoOfLargeScreenDTO> alarmInfoOfLargeScreenDTOS = alarmConvert.toAlarmInfoOfLargeScreenDTOList(data.getList());
        List<TblItem> itemList = itemAPI.searchItemByItemCodeList(data.getList().stream().map(TblAlarmRecordUnhandle::getItemCode).collect(Collectors.toList())).getData();



        for (AlarmInfoOfLargeScreenDTO largeScreen : alarmInfoOfLargeScreenDTOS) {

            // 匹配eye和center确定视角
            itemList.forEach(e->{
                if(e.getCode().equals(largeScreen.getItemCode())){
                    if(null != e.getViewLongitude()){
                        largeScreen.setEye(Arrays.stream(e.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                        largeScreen.setCenter(Arrays.stream(e.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                    }
                }
            });

            largeScreen.setAreaName(this.getAreaNameByAreaCode(largeScreen.getAreaCode()));
            largeScreen.setStayTime(timeGap(LocalDateTime.now(), largeScreen.getAlarmTime(), ChronoUnit.SECONDS));
            // todo 对接高博医院自身的信息发布系统后再写 largeScreen.setPublishContent();
        }
        pageInfoVO.setList(alarmInfoOfLargeScreenDTOS);
        return pageInfoVO;
    }



    /**
     * @Author: liwencai
     * @Description: 结合redis，根据区域编码查询区域名称
     * @Date: 2022/9/23
     * @Param areaCode:
     * @return: java.lang.String
     */
    public String getAreaNameByAreaCode(String areaCode) {
        String buildAreaName = null;
        try {
            // 在redis中查询缓存
            buildAreaName = redisOperationService.getBuildAreaNameByCode(areaCode);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        if (StringUtils.isBlank(buildAreaName)) {
            // 在数据库中查询
            try {
                String areaNameInDB = adminAPI.searchAreaNameByAreaCode(areaCode).getData();
                if (StringUtils.isNotBlank(areaCode)) {
                    // 数据库中存在，存入缓存中并返回
                    redisOperationService.saveBuildAreaCodeMapToName(areaCode, areaNameInDB);
                    return areaNameInDB;
                } else {
                    return null;
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                return null;
            }
        } else {
            return buildAreaName;
        }
    }

    /**
     * @Author: liwencai
     * @Description: 计算两时间的差值（单位/秒）
     * @Date: 2022/8/31
     * @Param startTime:
     * @Param endTime:
     * @return: java.lang.Long
     */
    public Long timeGap(LocalDateTime startTime,LocalDateTime endTime,ChronoUnit chronoUnit){
        try {
            return Math.abs(endTime.until(startTime, chronoUnit));
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }
}
