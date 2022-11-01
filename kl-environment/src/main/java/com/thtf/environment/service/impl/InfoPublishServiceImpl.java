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
import com.thtf.environment.dto.ItemPlayInfoDTO;
import com.thtf.environment.dto.PageInfoVO;
import com.thtf.environment.dto.convert.AlarmConvert;
import com.thtf.environment.dto.convert.ItemConvert;
import com.thtf.environment.dto.convert.PageInfoConvert;
import com.thtf.environment.service.InfoPublishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
        String parameterCode = null;
        String parameterValue = null;
        if(null != paramMap.get("onOffStatus")){
            parameterCode = "OnOffStatus";
            parameterValue = "1";
        }
        PageInfo<ItemNestedParameterVO> itemPageInfo = itemAPI.listItemNestedParametersBySysCodeAndItemCodeListAndParameterKeyAndValueAndKeywordPage(
                (String) paramMap.get("sysCode"), null,null,(String) paramMap.get("areaCode"),
                parameterCode, parameterValue, (String) paramMap.get("keyword"),
                (Integer) paramMap.get("pageNumber"), (Integer) paramMap.get("pageSize")
        ).getData();

        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(itemPageInfo);
        List<ItemNestedParameterVO> list = itemPageInfo.getList();

        // 获取设备报警信息
        List<TblAlarmRecordUnhandle> allAlarmRecordUnhandled = alarmAPI.getAlarmInfoByItemCodeListLimitOne(list.stream().map(ItemNestedParameterVO::getCode).collect(Collectors.toList())).getData();

        List<ItemInfoOfLargeScreenDTO> resultList = new ArrayList<>();

        for (ItemNestedParameterVO itemNestedParameterVO : list) {

            ItemInfoOfLargeScreenDTO innerResult = new ItemInfoOfLargeScreenDTO();
            innerResult.setItemId(itemNestedParameterVO.getId());
            innerResult.setItemCode(itemNestedParameterVO.getCode());
            innerResult.setItemName(itemNestedParameterVO.getName());
            innerResult.setAreaCode(itemNestedParameterVO.getAreaCode());
            innerResult.setAreaName(itemNestedParameterVO.getAreaName());
            innerResult.setBuildingCode(itemNestedParameterVO.getBuildingCode());

            if(StringUtils.isNotBlank(itemNestedParameterVO.getViewLongitude())){
                innerResult.setEye(Arrays.stream(itemNestedParameterVO.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }

            if(StringUtils.isNotBlank(itemNestedParameterVO.getViewLatitude())){
                innerResult.setCenter(Arrays.stream(itemNestedParameterVO.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }

            allAlarmRecordUnhandled.forEach(e->{
                if(e.getItemCode().equals(itemNestedParameterVO.getCode())){
                    innerResult.setAlarmStatus(e.getAlarmCategory() == 1?"故障报警":"监测报警");
                }
            });

            for (TblItemParameter p : itemNestedParameterVO.getParameterList()) {
                if ("OnOffStatus".equals(p.getParameterType())) {
                    innerResult.setRunParameterCode(p.getCode());
                    innerResult.setRunValue(p.getValue() + (p.getUnit() == null ? "" : p.getUnit()));
                }
                if ("OnlineStatus".equals(p.getParameterType())) {
                    innerResult.setOnlineParameterCode(p.getCode());
                    innerResult.setOnlineValue(p.getValue() + (p.getUnit() == null ? "" : p.getUnit()));
                }
                if ("Capacity".equals(p.getParameterType())) {
                    innerResult.setCapacityParameterCode(p.getCode());
                    innerResult.setCapacityValue(p.getValue() + (p.getUnit() == null ? "" : p.getUnit()));
                }
                if ("Luminance".equals(p.getParameterType())) {
                    innerResult.setLuminanceParameterCode(p.getCode());
                    innerResult.setLuminanceValue(p.getValue() + (p.getUnit() == null ? "" : p.getUnit()));
                }
                if ("Volume".equals(p.getParameterType())) {
                    innerResult.setVolumeParameterCode(p.getCode());
                    innerResult.setVolumeValue(p.getValue() + (p.getUnit() == null ? "" : p.getUnit()));
                }
                if ("StorageStatus".equals(p.getParameterType())) {
                    innerResult.setStorageStatusParameterCode(p.getCode());
                    innerResult.setStorageStatusValue(p.getValue() + (p.getUnit() == null ? "" : p.getUnit()));
                }
            }
            resultList.add(innerResult);
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
            largeScreen.setStayTime(getTimeGap(largeScreen.getAlarmTime(),LocalDateTime.now()));
            // todo 对接高博医院自身的信息发布系统后再写 largeScreen.setPublishContent();
        }
        pageInfoVO.setList(alarmInfoOfLargeScreenDTOS);
        return pageInfoVO;
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/1
     * @Param: sysCode:
     * @Param: itemCodeList:
     * @Return: java.lang.Boolean
     */
    @Override
    @Transactional
    public Boolean remoteSwitch(String sysCode, List<Long> itemCodeList) {
        redisOperationService.remoteSwitchItemStatusByItemIdList(itemCodeList);
        // todo 修改设备参数
        return true;
    }

    /**
     * @Author: liwencai
     * @Description: 新增播单
     * @Date: 2022/11/1
     * @Param: param:
     * @Return: java.lang.Boolean
     */
    @Override
    public Boolean insertPlayOrder(ItemPlayInfoDTO param) {
        redisOperationService.insertPlayOrder(param);
        return true;
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

    /**
     * @Author: liwencai
     * @Description: 获取时间差
     * @Date: 2022/10/27
     * @Param: startTime:
     * @Param: endTime:
     * @Return: java.lang.String
     */
    public String getTimeGap(LocalDateTime startTime,LocalDateTime endTime){
        Date nowDate = Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant());
        Date alarmTimeStartTime = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = nowDate.getTime() - alarmTimeStartTime.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒
        long sec = diff % nd % nh % nm /ns;
        // 输出结果
        return (day+"天"+hour + "小时" + min + "分" +sec+"秒");
    }
}
