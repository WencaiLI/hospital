package com.thtf.environment.service.impl;


import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.alarmserver.ListAlarmInfoLimitOneParamDTO;
import com.thtf.common.dto.itemserver.*;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.environment.common.Constant.ParameterConstant;
import com.thtf.environment.dto.ParameterInfoDTO;
import com.thtf.environment.dto.*;
import com.thtf.environment.dto.convert.PageInfoConvert;
import com.thtf.environment.dto.convert.ParameterConverter;
import com.thtf.environment.service.BroadcastService;
import org.apache.commons.lang3.StringUtils;
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
    ItemAPI itemAPI;
    @Resource
    AlarmAPI alarmAPI;
    @Resource
    PageInfoConvert pageInfoConvert;

    @Resource
    ParameterConverter parameterConverter;

    @Resource
    RedisOperationService redisOperationService;

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
        if(StringUtils.isBlank(buildingCodes)){
            buildingCodes = null;
        }
        if (StringUtils.isBlank(areaCode)){
            areaCode = null;
        }
        CountItemByParameterListDTO countItemByParameterListDTO = new CountItemByParameterListDTO();
        if(StringUtils.isNotBlank(buildingCodes)){
            countItemByParameterListDTO.setBuildingCodeList(Arrays.asList(buildingCodes.split(",")));
        }else {
            countItemByParameterListDTO.setAreaCode(areaCode);
        }
        countItemByParameterListDTO.setSysCode(sysCode);
        countItemByParameterListDTO.setParameterTypeCode(ParameterConstant.BROADCAST_TASK_STATUS);
        countItemByParameterListDTO.setParameterValue(ParameterConstant.BROADCAST_TASK_ON_VALUE);

        result.setMonitorNum(itemAPI.countItemByParameterList(countItemByParameterListDTO).getData());
        // 设备一般信息
        ItemTotalAndOnlineAndAlarmNumDTO state = itemAPI.getItemParameterAndTotalAndAlarmItemNumber(sysCode, buildingCodes, areaCode,
                ParameterConstant.BROADCAST_STATE, ParameterConstant.ON_OFF_STATUS_ON_VALUE).getData();
        result.setItemNum(state.getTotalNum());
        result.setRunningItemNum(state.getOnlineNum());
        result.setFaultItemNum(state.getMalfunctionAlarmNumber());
        ItemGroupOtherCountDTO data = itemAPI.countGroupByParameter(sysCode, ParameterConstant.BROADCAST_TASK_STATUS, ParameterConstant.BROADCAST_TASK_ON_VALUE).getData();

        if(null == data){
            result.setAreaNum(0); // todo liwencai 此处目前使用群控分组方式,等确定方式后确定实现方式
            result.setRunningAreaNum(0); // todo liwencai 此处目前使用群控分组方式,等确定方式后确定实现方式
        }else {
            result.setAreaNum(data.getTotalNum()); // todo liwencai 此处目前使用群控分组方式,等确定方式后确定实现方式
            result.setRunningAreaNum(data.getOtherNum()); // todo liwencai 此处目前使用群控分组方式,等确定方式后确定实现方式
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
    public PageInfoVO getItemInfo(String sysCode, String buildingCodes, String areaCode, String runVale, String keyword, Integer pageNumber, Integer pageSize) {
        String parameterCode = null;
        String parameterValue = null;
        if(StringUtils.isNotBlank(runVale)){
            parameterCode = ParameterConstant.BROADCAST_STATE;
            parameterValue = runVale;
        }
        PageInfo<ItemNestedParameterVO> itemPageInfo = itemAPI.listItemNestedParametersBySysCodeAndItemCodeListAndParameterKeyAndValueAndKeywordPage(
                sysCode, null, buildingCodes ,areaCode,parameterCode, parameterValue, keyword,pageNumber,pageSize).getData();

        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(itemPageInfo);
        // 获取设备报警信息
        // List<TblAlarmRecordUnhandle> allAlarmRecordUnhandled = alarmAPI.getAlarmInfoByItemCodeListLimitOne(list.stream().map(ItemNestedParameterVO::getCode).collect(Collectors.toList())).getData();

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
            itemInfoOfBroadcastDTO.setIpAddress("10.10.82.146");
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
            if (ParameterConstant.BROADCAST_STATE.equals(parameter.getParameterType())) {
                innerResult.setRunParameterCode(parameter.getCode());
                innerResult.setRunValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 在线状态
            if (ParameterConstant.BROADCAST_ONLINE.equals(parameter.getParameterType())) {
                innerResult.setOnlineParameterCode(parameter.getCode());
                innerResult.setOnlineValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 任务状态
            if (ParameterConstant.BROADCAST_TASK_STATUS.equals(parameter.getParameterType())) {
                innerResult.setTaskStatusParameterCode(parameter.getCode());
                innerResult.setTaskStatusValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 任务队列
            if (ParameterConstant.BROADCAST_TASK_QUEUE.equals(parameter.getParameterType())) {
                innerResult.setTaskQueueParameterCode(parameter.getCode());
                innerResult.setTaskQueueValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 音量
            if (ParameterConstant.BROADCAST_AUDIO.equals(parameter.getParameterType())) {
                innerResult.setAudioParameterCode(parameter.getCode());
                innerResult.setAudioValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 对讲状态
            if (ParameterConstant.BROADCAST_INTERCOM_STATUS.equals(parameter.getParameterType())) {
                innerResult.setIntercomStatusParameterCode(parameter.getCode());
                innerResult.setIntercomStatusValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 消防播报端口
            if (ParameterConstant.BROADCAST_PLAY_PORT.equals(parameter.getParameterType())) {
                innerResult.setPlayPortParameterCode(parameter.getCode());
                innerResult.setPlayPortValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 音量接收方式
            if (ParameterConstant.BROADCAST_AUDIO_CONTROL.equals(parameter.getParameterType())) {
                innerResult.setAudioReceiveParameterCode(parameter.getCode());
                innerResult.setAudioReceiveValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 报警
            if (ParameterConstant.BROADCAST_ALARM.equals(parameter.getParameterType())) {
                innerResult.setAlarmParameterCode(parameter.getCode());
                innerResult.setAlarmValue(parameter.getValue());
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 故障
            if (ParameterConstant.BROADCAST_FAULT.equals(parameter.getParameterType())) {
                innerResult.setFaultParameterCode(parameter.getCode());
                innerResult.setFaultValue(parameter.getValue());
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
    public PageInfoVO getAlarmInfo(String keyword, String sysCode, String buildingCodes, String areaCode, Integer pageNumber, Integer pageSize) {

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

        PageInfo<TblAlarmRecordUnhandle> tblAlarmRecordUnhandlePageInfo = alarmAPI.listAlarmInfoLimitOnePage(listAlarmInfoLimitOneParamDTO).getData();
        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(tblAlarmRecordUnhandlePageInfo);
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
        if(null == itemNestedParameterVOList || itemNestedParameterVOList.size()<1){
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
