package com.thtf.environment.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.github.pagehelper.PageInfo;
import com.thtf.common.constant.AlarmConstants;
import com.thtf.common.dto.alarmserver.ListAlarmInfoLimitOneParamDTO;
import com.thtf.common.dto.itemserver.*;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.environment.config.ItemParameterConfig;
import com.thtf.environment.dto.ParameterInfoDTO;
import com.thtf.environment.dto.*;
import com.thtf.environment.dto.convert.PageInfoConvert;
import com.thtf.environment.dto.convert.ParameterConverter;
import com.thtf.environment.service.BroadcastService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
/**
 * @Author: liwencai
 * @Date: 2022/10/7 13:42
 * @Description: 广播接口
 */
@Service
public class BroadcastServiceImpl implements BroadcastService {

    @Resource
    private ItemAPI itemAPI;

    @Resource
    private AlarmAPI alarmAPI;

    @Resource
    private PageInfoConvert pageInfoConvert;

    @Resource
    private ParameterConverter parameterConverter;

    @Resource
    private RedisOperationService redisOperationService;

    @Resource
    private CommonService commonService;

    @Resource
    private ItemParameterConfig itemParameterConfig;

    /**
     * @Author: liwencai
     * @Description: 前端数据展示
     * @Date: 2022/10/7
     * @Param sysCode: 子系统编码
     * @Param itemType: 设备类别编码
     * @return: java.util.List<com.thtf.broadcast.dto.DisplayInfoDTO>
     */
    @Override
    public DisplayInfoDTO displayInfo(String sysCode, String buildingCodes,String areaCode) {
        DisplayInfoDTO result = new DisplayInfoDTO();
        List<String> buildingCodeList = null;
        List<String> areaCodeList = null;
        if(StringUtils.isNotBlank(areaCode)){
            areaCodeList = Arrays.asList(areaCode.split(","));
        }else {
            if(StringUtils.isNotBlank(buildingCodes)){
                buildingCodeList = Arrays.asList(buildingCodes.split(","));
            }
        }
        // 设备总数 报警设备数 故障设备总数
        CountItemInfoParamDTO countItemInfoParam = new CountItemInfoParamDTO();
        countItemInfoParam.setSysCode(sysCode);
        countItemInfoParam.setBuildingCodeList(buildingCodeList);
        countItemInfoParam.setAreaCodeList(areaCodeList);
        CountItemInfoResultDTO itemInfo = itemAPI.countItemInfo(countItemInfoParam).getData();
        result.setItemNum(itemInfo.getItemNumber());
        result.setMonitorNum(itemInfo.getAlarmItemNumber());
        result.setFaultItemNum(itemInfo.getFaultItemNumber());
        // 在线数
        CountItemByParameterListDTO countItemByParameterListDTO = new CountItemByParameterListDTO();
        if(null != buildingCodeList && buildingCodeList.size()>0){
            countItemByParameterListDTO.setBuildingCodeList(buildingCodeList);
        }else {
            countItemByParameterListDTO.setAreaCode(areaCode);
        }
        countItemByParameterListDTO.setSysCode(sysCode);
        countItemByParameterListDTO.setParameterTypeCode(itemParameterConfig.getBroadcastOnline());
        String parameterValueOnline = commonService.getParameterValueByStateExplain(sysCode, itemParameterConfig.getBroadcastOnline(), null, new String[]{"在线","在","上"});
        countItemByParameterListDTO.setParameterValue(parameterValueOnline);
        result.setRunningItemNum(itemAPI.countItemByParameterList(countItemByParameterListDTO).getData());
        // 群控分组信息
        // todo liwencai 此处目前使用群控分组方式,等确定方式后确定实现方式
        ItemGroupOtherCountDTO data = itemAPI.countGroupByParameter(sysCode,buildingCodes,areaCode, itemParameterConfig.getBroadcastOnline(), parameterValueOnline).getData();
        if(null == data){
            result.setAreaNum(0);
            result.setRunningAreaNum(0);
        }else {
            result.setAreaNum(data.getTotalNum());
            result.setRunningAreaNum(data.getOtherNum());
        }
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 获取控制信息
     * @Date: 2022/10/7
     * @Param sysCode:
     * @return: java.util.List<com.thtf.environment.dto.KeyValueDTO>
     */
    @Override
    public List<KeyValueDTO> controlInfo(String sysCode) {
        List<KeyValueDTO> resultList = new ArrayList<>();
        // 终端监听的设备总数
        KeyValueDTO keyValueDTO_monitor = new KeyValueDTO();
        keyValueDTO_monitor.setKey("终端监听");
        // todo
        keyValueDTO_monitor.setValue(1);
        resultList.add(keyValueDTO_monitor);
        // 远程控制设备总数
        KeyValueDTO keyValueDTO_control = new KeyValueDTO();
        keyValueDTO_control.setKey("终端监听");
        // todo
        keyValueDTO_control.setValue(1);
        resultList.add(keyValueDTO_control);
        return resultList;
    }


    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/10/7
     * @Param keyword:
     * @Param sysCode:
     * @Param areaCode:
     * @Param pageNumber:
     * @Param pageSize:
     * @return: com.thtf.environment.dto.PageInfoVO
     */
    @Override
    public PageInfo<ItemInfoOfBroadcastDTO> getItemInfo(String sysCode, String buildingCodes, String areaCode, String runVale, String keyword, Integer pageNumber, Integer pageSize) {
        PageInfo<ItemInfoOfBroadcastDTO> pageInfoVO = new PageInfo<>();
        String parameterCode = null;
        String parameterValue = null;
        if(StringUtils.isNotBlank(runVale)){

            String parameterValueOnline = commonService.getParameterValueByStateExplain(sysCode, itemParameterConfig.getBroadcastOnline(), null, new String[]{"在线","在","上"});
            parameterCode = itemParameterConfig.getBroadcastOnline();
            parameterValue = parameterValueOnline;
        }
        PageInfo<ItemNestedParameterVO> itemPageInfo = itemAPI.listItemNestedParametersBySysCodeAndItemCodeListAndParameterKeyAndValueAndKeywordPage(
                sysCode, null, buildingCodes ,areaCode,parameterCode, parameterValue, keyword,pageNumber,pageSize).getData();
        // PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(itemPageInfo);
        BeanUtils.copyProperties(itemPageInfo,pageInfoVO);
        // 获取设备报警信息
        List<ItemInfoOfBroadcastDTO> resultList = new ArrayList<>();
        // 获取设备基本信息
        for (ItemNestedParameterVO item : itemPageInfo.getList()) {
            ItemInfoOfBroadcastDTO itemInfoOfBroadcastDTO = new ItemInfoOfBroadcastDTO();
            itemInfoOfBroadcastDTO.setItemId(item.getId());
            itemInfoOfBroadcastDTO.setItemCode(item.getCode());
            itemInfoOfBroadcastDTO.setItemName(item.getName());
            itemInfoOfBroadcastDTO.setAreaCode(item.getAreaCode());
            itemInfoOfBroadcastDTO.setAreaName(item.getAreaName());
            itemInfoOfBroadcastDTO.setBuildingCode(item.getBuildingCode());
            // todo 怎么获取IP地址存疑
            itemInfoOfBroadcastDTO.setIpAddress("127.0.0.1");
            // 配置模型视角
            if(StringUtils.isNotBlank(item.getViewLongitude())){
                itemInfoOfBroadcastDTO.setEye(Arrays.stream(item.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            if(StringUtils.isNotBlank(item.getViewLatitude())){
                itemInfoOfBroadcastDTO.setCenter(Arrays.stream(item.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            convertToItemInfoOfLargeScreenDTO(itemInfoOfBroadcastDTO,item.getParameterList());
            resultList.add(itemInfoOfBroadcastDTO);
        }
        pageInfoVO.setList(resultList);
        return pageInfoVO;
    }

    public void convertToItemInfoOfLargeScreenDTO(ItemInfoOfBroadcastDTO innerResult ,List<TblItemParameter> parameterList){
        List<ParameterInfoDTO> parameterInnerList = new ArrayList<>();
        for (TblItemParameter parameter : parameterList) {
            // 运行状态
            if (itemParameterConfig.getState().equals(parameter.getParameterType())) {
                innerResult.setRunParameterCode(parameter.getCode());
                innerResult.setRunValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 报警
            if (itemParameterConfig.getAlarm().equals(parameter.getParameterType())) {
                innerResult.setAlarmParameterCode(parameter.getCode());
                innerResult.setAlarmValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 故障
            if (itemParameterConfig.getFault().equals(parameter.getParameterType())) {
                innerResult.setFaultParameterCode(parameter.getCode());
                innerResult.setFaultValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 在线状态
            if (itemParameterConfig.getBroadcastOnline().equals(parameter.getParameterType())) {
                innerResult.setOnlineParameterCode(parameter.getCode());
                innerResult.setOnlineValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 任务状态
            if (itemParameterConfig.getBroadcastTaskStatus().equals(parameter.getParameterType())) {
                innerResult.setTaskStatusParameterCode(parameter.getCode());
                innerResult.setTaskStatusValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 任务队列
            if (itemParameterConfig.getBroadcastTaskQueue().equals(parameter.getParameterType())) {
                innerResult.setTaskQueueParameterCode(parameter.getCode());
                innerResult.setTaskQueueValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 音量
            if (itemParameterConfig.getBroadcastAudio().equals(parameter.getParameterType())) {
                innerResult.setAudioParameterCode(parameter.getCode());
                innerResult.setAudioValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 对讲状态
            if (itemParameterConfig.getBroadcastIntercomStatus().equals(parameter.getParameterType())) {
                innerResult.setIntercomStatusParameterCode(parameter.getCode());
                innerResult.setIntercomStatusValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 消防播报端口
            if (itemParameterConfig.getBroadcastPlayPort().equals(parameter.getParameterType())) {
                innerResult.setPlayPortParameterCode(parameter.getCode());
                innerResult.setPlayPortValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 音量控制
            if (itemParameterConfig.getBroadcastAudioCtrl().equals(parameter.getParameterType())) {
                innerResult.setAudioReceiveParameterCode(parameter.getCode());
                innerResult.setAudioReceiveValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

        }
        innerResult.setParameterList(parameterInnerList);
    }


    /**
     * @Author: liwencai
     * @Description: 获取故障信息
     * @Date: 2022/10/7
     * @Param keyword:
     * @Param sysCode:
     * @Param pageNumber:
     * @Param pageSize:
     * @return: com.thtf.environment.dto.PageInfoVO
     */
    @Override
    public PageInfo<AlarmInfoOfBroadcastDTO> getAlarmInfo(String keyword, String sysCode, String buildingCodes, String areaCode, Integer pageNumber, Integer pageSize) {
        PageInfo<AlarmInfoOfBroadcastDTO> pageInfoVO = new PageInfo<>();
        List<String> buildingCodeList = null;
        List<String> areaCodeList = null;
        if(StringUtils.isNotBlank(areaCode)){
            areaCodeList = Arrays.asList(areaCode.split(","));
        }else {
            if(StringUtils.isNotBlank(buildingCodes)){
                buildingCodeList = Arrays.asList(buildingCodes.split(","));
            }
        }
        TblItem tblItem = new TblItem();
        tblItem.setBuildingCodeList(buildingCodeList);
        tblItem.setAreaCodeList(areaCodeList);
        tblItem.setSystemCode(sysCode);
        tblItem.setFault(1);
        List<TblItem> itemList = itemAPI.queryAllItems(tblItem).getData();
        if(CollectionUtils.isEmpty(itemList)){
            return pageInfoVO;
        }
        List<String> itemCodeList = itemList.stream().map(TblItem::getCode).collect(Collectors.toList());

        ListAlarmInfoLimitOneParamDTO listAlarmInfoLimitOneParamDTO = new ListAlarmInfoLimitOneParamDTO();
        listAlarmInfoLimitOneParamDTO.setSystemCode(sysCode);
        listAlarmInfoLimitOneParamDTO.setAlarmCategory(AlarmConstants.FAULT_CATEGORY_INTEGER.toString());
        listAlarmInfoLimitOneParamDTO.setItemCodeList(itemCodeList);
        listAlarmInfoLimitOneParamDTO.setKeyword(keyword);
        listAlarmInfoLimitOneParamDTO.setKeywordOfItemName(keyword);
        listAlarmInfoLimitOneParamDTO.setKeywordOfItemCode(keyword);
        listAlarmInfoLimitOneParamDTO.setKeywordOfAlarmDesc(keyword);
        listAlarmInfoLimitOneParamDTO.setPageNumber(pageNumber);
        listAlarmInfoLimitOneParamDTO.setPageSize(pageSize);

        PageInfo<TblAlarmRecordUnhandle> tblAlarmRecordUnhandlePageInfo = alarmAPI.listAlarmInfoLimitOnePage(listAlarmInfoLimitOneParamDTO).getData();
        // PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(tblAlarmRecordUnhandlePageInfo);
        BeanUtils.copyProperties(tblAlarmRecordUnhandlePageInfo,pageInfoVO);
        List<String> collect = tblAlarmRecordUnhandlePageInfo.getList().stream().map(TblAlarmRecordUnhandle::getItemCode).collect(Collectors.toList());
        itemList.removeIf(e->!collect.contains(e.getCode()));
        List<AlarmInfoOfBroadcastDTO> resultList = new ArrayList<>();

        // todo 怎么获取IP地址存疑
        for (TblAlarmRecordUnhandle alarm : tblAlarmRecordUnhandlePageInfo.getList()) {
            AlarmInfoOfBroadcastDTO alarmInfoOfBroadcastDTO = new AlarmInfoOfBroadcastDTO();
            alarmInfoOfBroadcastDTO.setAlarmId(alarm.getId());
            alarmInfoOfBroadcastDTO.setAlarmCategory(alarm.getAlarmCategory());
            alarmInfoOfBroadcastDTO.setAlarmLevel(alarm.getAlarmLevel());
            alarmInfoOfBroadcastDTO.setAlarmTime(alarm.getAlarmTime());

            for (TblItem tblItemDTO : itemList) {
                if(tblItemDTO.getCode().equals(alarm.getItemCode())){
                    alarmInfoOfBroadcastDTO.setItemId(tblItemDTO.getId());
                    alarmInfoOfBroadcastDTO.setItemCode(tblItemDTO.getCode());
                    alarmInfoOfBroadcastDTO.setItemName(tblItemDTO.getName());
                    alarmInfoOfBroadcastDTO.setAreaCode(tblItemDTO.getAreaCode());
                    alarmInfoOfBroadcastDTO.setAreaName(tblItemDTO.getAreaName());
                    long duration = LocalDateTimeUtil.between(alarmInfoOfBroadcastDTO.getAlarmTime(), LocalDateTime.now(), ChronoUnit.MILLIS);
                    alarmInfoOfBroadcastDTO.setStayTime(DateUtil.formatBetween(duration, BetweenFormatter.Level.SECOND));
                    alarmInfoOfBroadcastDTO.setBuildingCode(tblItemDTO.getBuildingCode());
                }
            }
            resultList.add(alarmInfoOfBroadcastDTO);
        }
        pageInfoVO.setList(resultList);
        return pageInfoVO;
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/3
     * @Param: itemCode: 设备编码
     * @Return: java.util.List<com.thtf.environment.dto.BroadcastPublishContentDTO>
     * @return
     */
    @Override
    public List<BroadcastContentInsertDTO> getPublishContent(String itemCode) {
        return redisOperationService.listBroadcastContent(itemCode);
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/3
     * @Param: param:
     * @Return: java.lang.Boolean
     */
    @Override
    public Boolean publishContent(BroadcastContentInsertDTO param) {
        return redisOperationService.publishBroadcastContent(param);
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/12/1
     * @Param sysCode:
     * @Param itemCodes:
     * @return: com.thtf.environment.dto.ItemInfoOfLargeScreenDTO
     */
    @Override
    public ItemInfoOfBroadcastDTO getMonitorPoint(String sysCode, String itemCode) {
        List<ItemNestedParameterVO> itemNestedParameterVOList = itemAPI.searchItemNestedParametersBySysCodeAndItemCodeList(sysCode, Collections.singletonList(itemCode)).getData();
        if(CollectionUtils.isEmpty(itemNestedParameterVOList)){
            return null;
        }
        ItemNestedParameterVO itemNestedParameterVO = itemNestedParameterVOList.get(0);
        ItemInfoOfBroadcastDTO innerResult = new ItemInfoOfBroadcastDTO();
        innerResult.setItemId(itemNestedParameterVO.getId());
        innerResult.setItemCode(itemNestedParameterVO.getCode());
        innerResult.setItemName(itemNestedParameterVO.getName());
        innerResult.setAreaCode(itemNestedParameterVO.getAreaCode());
        innerResult.setAreaName(itemNestedParameterVO.getAreaName());
        innerResult.setBuildingCode(itemNestedParameterVO.getBuildingCode());
        innerResult.setIpAddress("127.0.0.1");
        if(null != itemNestedParameterVO.getViewLongitude()){
            innerResult.setEye(Arrays.stream(itemNestedParameterVO.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
        }
        if(null != itemNestedParameterVO.getViewLatitude()){
            innerResult.setCenter(Arrays.stream(itemNestedParameterVO.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
        }
        this.convertToItemInfoOfLargeScreenDTO(innerResult,itemNestedParameterVO.getParameterList());
        return innerResult;
    }
}
