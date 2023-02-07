package com.thtf.environment.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.alarmserver.ListAlarmInfoLimitOneParamDTO;
import com.thtf.common.dto.itemserver.CountItemByParameterListDTO;
import com.thtf.common.dto.itemserver.CountItemInfoParamDTO;
import com.thtf.common.dto.itemserver.CountItemInfoResultDTO;
import com.thtf.common.dto.itemserver.ItemNestedParameterVO;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.entity.itemserver.TblVideoItem;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.environment.common.Constant.ParameterConstant;
import com.thtf.environment.dto.*;
import com.thtf.environment.dto.convert.AlarmConvert;
import com.thtf.environment.dto.convert.PageInfoConvert;
import com.thtf.environment.dto.convert.ParameterConverter;
import com.thtf.environment.service.InfoPublishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
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

    @Resource
    private ItemAPI itemAPI;
    @Resource
    private AlarmAPI alarmAPI;
    @Resource
    private AdminAPI adminAPI;
    @Resource
    PageInfoConvert pageInfoConvert;
    @Resource
    ParameterConverter parameterConverter;
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
        // 运行状态筛选
        if(null != paramMap.get(ParameterConstant.INFO_PUBLISH_ONLINE_STATUS)){
            parameterCode = ParameterConstant.INFO_PUBLISH_ONLINE_STATUS;
            parameterValue = (String) paramMap.get(ParameterConstant.INFO_PUBLISH_ONLINE_STATUS);
        }
        // 查询所有设备信息
        PageInfo<ItemNestedParameterVO> itemPageInfo = itemAPI.listItemNestedParametersBySysCodeAndItemCodeListAndParameterKeyAndValueAndKeywordPage(
                (String) paramMap.get("sysCode"), null, (String) paramMap.get("buildingCodes") ,(String) paramMap.get("areaCode"),
                parameterCode, parameterValue , (String) paramMap.get("keyword"),
                (Integer) paramMap.get("pageNumber"), (Integer) paramMap.get("pageSize")
        ).getData();
        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(itemPageInfo);
        List<ItemNestedParameterVO> list = itemPageInfo.getList();
        // 获取所有设备报警信息
        List<ItemInfoOfLargeScreenDTO> resultList = new ArrayList<>();
        for (ItemNestedParameterVO itemNestedParameterVO : list) {
            ItemInfoOfLargeScreenDTO innerResult = new ItemInfoOfLargeScreenDTO();
            innerResult.setItemId(itemNestedParameterVO.getId());
            innerResult.setItemCode(itemNestedParameterVO.getCode());
            innerResult.setItemName(itemNestedParameterVO.getName());
            innerResult.setAreaCode(itemNestedParameterVO.getAreaCode());
            innerResult.setAreaName(itemNestedParameterVO.getAreaName());
            innerResult.setBuildingCode(itemNestedParameterVO.getBuildingCode());
            innerResult.setAlarmStatus(itemNestedParameterVO.getFault());
            // 匹配模型视角
            if(StringUtils.isNotBlank(itemNestedParameterVO.getViewLongitude())){
                innerResult.setEye(Arrays.stream(itemNestedParameterVO.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            if(StringUtils.isNotBlank(itemNestedParameterVO.getViewLatitude())){
                innerResult.setCenter(Arrays.stream(itemNestedParameterVO.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            this.convertToItemInfoOfLargeScreenDTO(innerResult,itemNestedParameterVO.getParameterList());
            resultList.add(innerResult);
        }
        pageInfoVO.setList(resultList);
        return pageInfoVO;
    }

    public void convertToItemInfoOfLargeScreenDTO(ItemInfoOfLargeScreenDTO innerResult ,List<TblItemParameter> parameterList){
        List<ParameterInfoDTO> parameterInnerList = new ArrayList<>();
        for (TblItemParameter parameter : parameterList) {
            // 容量百分比
            if (ParameterConstant.INFO_PUBLISH_RUN_STATUS.equals(parameter.getParameterType())) {
                innerResult.setRunParameterCode(parameter.getCode());
                innerResult.setRunValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 在线状态
            if (ParameterConstant.INFO_PUBLISH_ONLINE_STATUS.equals(parameter.getParameterType())) {
                innerResult.setOnlineParameterCode(parameter.getCode());
                innerResult.setOnlineValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 亮度
            if (ParameterConstant.INFO_PUBLISH_LUMINANCE.equals(parameter.getParameterType())) {
                innerResult.setLuminanceParameterCode(parameter.getCode());
                innerResult.setLuminanceValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 音量
            if (ParameterConstant.INFO_PUBLISH_VOLUME.equals(parameter.getParameterType())) {
                innerResult.setVolumeParameterCode(parameter.getCode());
                innerResult.setVolumeValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 放映时长
            if (ParameterConstant.INFO_PUBLISH_RUN_TIME.equals(parameter.getParameterType())) {
                innerResult.setShowDurationParameterCode(parameter.getCode());
                innerResult.setShowDurationValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 总容量
            if (ParameterConstant.INFO_PUBLISH_CAPACITY.equals(parameter.getParameterType())) {
                innerResult.setCapacityParameterCode(parameter.getCode());
                innerResult.setCapacityValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 已使用容量和所占百分比
            if (ParameterConstant.INFO_PUBLISH_STORED_CAPACITY.equals(parameter.getParameterType())) {
                innerResult.setStorageStatusParameterCode(parameter.getCode());
                innerResult.setStorageStatusValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
        }
        innerResult.setParameterList(parameterInnerList);
    }

    /**
     * @Author: liwencai
     * @Description: 获取信息发布大屏报警信息
     * @Date: 2022/9/23
     * @Param sysCode: 子系统编码
     * @Param keyword: 关键词
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: java.util.List<com.thtf.environment.dto.AlarmInfoOfLargeScreenDTO>
     */
    @Override
    public PageInfoVO getLargeScreenAlarmInfo(String sysCode, String buildingCodes, String areaCode, String keyword, Integer pageNumber, Integer pageSize) {
        List<String> buildingCodeList = null;
        List<String> areaCodeList = null;
        if(StringUtils.isNotBlank(buildingCodes)){
            buildingCodeList = Arrays.asList(buildingCodes.split(","));
        }else {
            if(StringUtils.isNotBlank(areaCode)){
                areaCodeList = Arrays.asList(areaCode.split(","));
            }
        }
        TblItem tblItem = new TblItem();
        tblItem.setBuildingCodeList(buildingCodeList);
        tblItem.setAreaCodeList(areaCodeList);
        tblItem.setSystemCode(sysCode);
        tblItem.setFault(1);
        List<TblItem> itemList = itemAPI.queryAllItems(tblItem).getData();
        if(itemList ==  null || itemList.size() == 0){
            return null;
        }
        List<String> itemCodeList = itemList.stream().map(TblItem::getCode).collect(Collectors.toList());

        ListAlarmInfoLimitOneParamDTO listAlarmInfoLimitOneParamDTO = new ListAlarmInfoLimitOneParamDTO();
        listAlarmInfoLimitOneParamDTO.setSystemCode(sysCode);
        listAlarmInfoLimitOneParamDTO.setAlarmCategory("1");
        listAlarmInfoLimitOneParamDTO.setItemCodeList(itemCodeList);
        listAlarmInfoLimitOneParamDTO.setKeyword(keyword);
        listAlarmInfoLimitOneParamDTO.setKeywordOfItemName(keyword);
        listAlarmInfoLimitOneParamDTO.setKeywordOfItemCode(keyword);
        listAlarmInfoLimitOneParamDTO.setKeywordOfAlarmDesc(keyword);
        listAlarmInfoLimitOneParamDTO.setPageNumber(pageNumber);
        listAlarmInfoLimitOneParamDTO.setPageSize(pageSize);
        PageInfo<TblAlarmRecordUnhandle> data = alarmAPI.listAlarmInfoLimitOnePage(listAlarmInfoLimitOneParamDTO).getData();
        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(data);
        List<AlarmInfoOfLargeScreenDTO> alarmInfoOfLargeScreenDTOS = alarmConvert.toAlarmInfoOfLargeScreenDTOList(data.getList());
        List<String> collect = data.getList().stream().map(TblAlarmRecordUnhandle::getItemCode).collect(Collectors.toList());
        itemList.removeIf(e->!collect.contains(e.getCode()));
        for (AlarmInfoOfLargeScreenDTO largeScreen : alarmInfoOfLargeScreenDTOS) {
            // 匹配eye和center确定视角
            itemList.forEach(e->{
                if(e.getCode().equals(largeScreen.getItemCode())){
                    if(null != e.getViewLongitude()){
                        largeScreen.setEye(Arrays.stream(e.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                    }
                    if(null != e.getViewLatitude()){
                        largeScreen.setCenter(Arrays.stream(e.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                    }
                }
            });
            largeScreen.setAreaName(this.getAreaNameByAreaCode(largeScreen.getAreaCode()));
            long duration = LocalDateTimeUtil.between(largeScreen.getAlarmTime(), LocalDateTime.now(), ChronoUnit.MILLIS);
            largeScreen.setStayTime(DateUtil.formatBetween(duration, BetweenFormatter.Level.SECOND));
            // largeScreen.setStayTime(getTimeGap(largeScreen.getAlarmTime(),LocalDateTime.now()));
            // todo 对接高博医院自身的信息发布系统后再写 largeScreen.setPublishContent();
        }
        pageInfoVO.setList(alarmInfoOfLargeScreenDTOS);
        return pageInfoVO;
    }

    /**
     * @Author: liwencai
     * @Description: 远程开关
     * @Date: 2022/11/1
     * @Param: sysCode: 子系统编码
     * @Param: itemCodeList: 设备编码集
     * @Return: java.lang.Boolean
     */
    @Override
    @Transactional
    public Boolean remoteSwitch(String sysCode, String itemCodes) {
        if( itemAPI.negateBooleanParameter(itemCodes,ParameterConstant.INFO_PUBLISH_RUN_STATUS).getData()){
            redisOperationService.remoteSwitchItemStatusByItemCodeList(Arrays.stream(itemCodes.split(",")).collect(Collectors.toList()));
        }
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
     * @Description: 获取信息发布大屏内容
     * @Date: 2022/11/2
     * @Param: sysCode: 子系统编码
     * @Param: buildingCodes: 建筑编码集
     * @Param: areaCode: 区域编码
     * @Param: itemCodes: 设备编码集
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.environment.dto.ItemPlayInfoDTO>>
     */
    @Override
    public List<ItemPlayInfoDTO> listLargeScreenContent(String sysCode, String buildingCodes, String areaCode, String itemCodes) {
        List<ItemPlayInfoDTO> resultList = new ArrayList<>();
        // 根据区域和子系统获取和建筑编码获取大屏系统
        TblItem tblItem = new TblItem();
        tblItem.setSystemCode(sysCode);
        if(StringUtils.isNotBlank(buildingCodes)){
            tblItem.setBuildingCodeList(Arrays.asList(buildingCodes.split(",")));
        }else {
            tblItem.setAreaCode(areaCode);
        }
        if(StringUtils.isNotBlank(itemCodes)){
            tblItem.setCodeList(Collections.singletonList(itemCodes));
        }
        List<TblItem> itemList = itemAPI.queryAllItems(tblItem).getData();
        // 获取大屏id
        List<String> itemCodeList = itemList.stream().map(TblItem::getCode).collect(Collectors.toList());
        for (String itemCode : itemCodeList) {
            ItemPlayInfoDTO result;
            // 从redis里获取缓存的
            try {
                List<ItemPlayInfoDTO> playOrderByItemCode = redisOperationService.getPlayOrderByItemCode(itemCode);
                if (playOrderByItemCode != null && playOrderByItemCode.size() > 0) {
                    result = playOrderByItemCode.get(0);
                    List<TblVideoItem> data = itemAPI.getVideoItemListByItemCode(itemCode).getBody().getData();
                    if(null != data && data.size()>0){ // todo 获取方式存在问题
                        result.setVideoItemInfo(data.get(0));
                    }
                    resultList.add(result);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return resultList;
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/30
     * @Param sysCode: 子系统编码
     * @Param itemCode: 设备编码
     * @return: com.thtf.environment.dto.InfoPublishPointDTO
     */
    @Override
    public ItemInfoOfLargeScreenDTO getMonitorPoint(String sysCode, String itemCode) {
        List<ItemNestedParameterVO> itemNestedParameterVOList = itemAPI.searchItemNestedParametersBySysCodeAndItemCodeList(sysCode, Collections.singletonList(itemCode)).getData();
        if(null == itemNestedParameterVOList || itemNestedParameterVOList.size()<1){
            return null;
        }
        ItemNestedParameterVO itemNestedParameterVO = itemNestedParameterVOList.get(0);
        ItemInfoOfLargeScreenDTO innerResult = new ItemInfoOfLargeScreenDTO();
        innerResult.setItemId(itemNestedParameterVO.getId());
        innerResult.setItemCode(itemNestedParameterVO.getCode());
        innerResult.setItemName(itemNestedParameterVO.getName());
        innerResult.setAreaCode(itemNestedParameterVO.getAreaCode());
        innerResult.setAreaName(itemNestedParameterVO.getAreaName());
        innerResult.setBuildingCode(itemNestedParameterVO.getBuildingCode());
        if(null != itemNestedParameterVO.getViewLongitude()){
            innerResult.setEye(Arrays.stream(itemNestedParameterVO.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
        }
        if(null != itemNestedParameterVO.getViewLatitude()){
            innerResult.setCenter(Arrays.stream(itemNestedParameterVO.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
        }
        this.convertToItemInfoOfLargeScreenDTO(innerResult,itemNestedParameterVO.getParameterList());
        return innerResult;
    }

    /**
     * @Author: liwencai
     * @Description: 前端展示数据
     * @Date: 2023/1/30
     * @Param sysCode:
     * @Param buildingCodes:
     * @Param areaCode:
     * @Param itemTypeCodes:
     * @Return: com.thtf.environment.dto.InfoPublishDisplayDTO
     */
    @Override
    public InfoPublishDisplayDTO getDisplayInfo(String sysCode, String buildingCodes, String areaCode, String itemTypeCodes) {
        InfoPublishDisplayDTO result = new InfoPublishDisplayDTO();

        CountItemByParameterListDTO countItemByParameterListDTO = new CountItemByParameterListDTO();
        countItemByParameterListDTO.setSysCode(sysCode);
        List<String> buildingCodeList = null;
        List<String> itemTypeCodeList = null;
        if(StringUtils.isNotBlank(buildingCodes)){
            buildingCodeList = Arrays.asList(buildingCodes.split(","));
        }else {
            countItemByParameterListDTO.setAreaCode(areaCode);
        }
        if (StringUtils.isNotBlank(itemTypeCodes)){
            itemTypeCodeList = Arrays.asList(itemTypeCodes.split(","));
        }
        countItemByParameterListDTO.setBuildingCodeList(buildingCodeList);
        countItemByParameterListDTO.setItemTypeCodeList(itemTypeCodeList);

        // 查看在线数量
        countItemByParameterListDTO.setParameterTypeCode(ParameterConstant.INFO_PUBLISH_ONLINE_STATUS);
        countItemByParameterListDTO.setParameterValue("1");
        Integer onlineCount = itemAPI.countItemByParameterList(countItemByParameterListDTO).getData();
        result.setOnlineCount(onlineCount);

        // 查看开启数量
        countItemByParameterListDTO.setParameterTypeCode(ParameterConstant.INFO_PUBLISH_RUN_STATUS);
        countItemByParameterListDTO.setParameterValue("1");
        Integer onCount = itemAPI.countItemByParameterList(countItemByParameterListDTO).getData();
        result.setOnCount(onCount);

        CountItemInfoParamDTO countItemInfoParamDTO = new CountItemInfoParamDTO();
        countItemInfoParamDTO.setSysCode(sysCode);
        if(null != buildingCodeList){
            countItemInfoParamDTO.setBuildingCodeList(buildingCodeList);
        }else {
            if(StringUtils.isNotBlank(areaCode)){
                countItemInfoParamDTO.setAreaCodeList(Collections.singletonList(areaCode));
            }
        }
        CountItemInfoResultDTO itemInfo = itemAPI.countItemInfo(countItemInfoParamDTO).getData();
        result.setTotalCount(itemInfo.getItemNumber());
        result.setFaultNumber(itemInfo.getFaultItemNumber());
        result.setAlarmNumber(itemInfo.getAlarmItemNumber());
        return result;
    }

    /* *************************** 复用代码区域 开始 ************************** */

    /**
     * @Author: liwencai
     * @Description: 结合redis，根据区域编码查询区域名称
     * @Date: 2022/9/23
     * @Param areaCode: 区域编码
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
     * @Param startTime: 开始时间
     * @Param endTime: 结束时间
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
     * @Description: 获取时间差（*天*小时*分*秒）
     * @Date: 2022/10/27
     * @Param: startTime: 开始时间
     * @Param: endTime: 结束使劲按
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
    /* *************************** 复用代码区域 结束 ************************** */
}
