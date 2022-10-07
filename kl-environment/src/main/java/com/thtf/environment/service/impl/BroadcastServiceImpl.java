package com.thtf.environment.service.impl;


import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.itemserver.TblItemDTO;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.environment.dto.*;
import com.thtf.environment.dto.convert.PageInfoConvert;
import com.thtf.environment.service.BroadcastService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liwencai
 * @Date: 2022/10/7 13:42
 * @Description:
 */
@Service
public class BroadcastServiceImpl implements BroadcastService {
    @Resource
    AdminAPI adminAPI;
    @Resource
    ItemAPI itemAPI;
    @Resource
    AlarmAPI alarmAPI;
    @Resource
    PageInfoConvert pageInfoConvert;

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
    public List<DisplayInfoDTO> displayInfo(String sysCode, String itemType) {
        List<DisplayInfoDTO> resultList = new ArrayList<>();

        // 广播区域总数 播放中区域
        DisplayInfoDTO displayInfoDTO_area = new DisplayInfoDTO();
        List<KeyValueDTO> keyValueDTOList_area = new ArrayList<>();

        KeyValueDTO keyValue_area_total = new KeyValueDTO();
        keyValue_area_total.setKey("广播区域总数");
        keyValue_area_total.setValue(1);
        keyValueDTOList_area.add(keyValue_area_total);

        KeyValueDTO keyValue_area_using = new KeyValueDTO();
        keyValue_area_using.setKey("播放中区域");
        keyValue_area_using.setValue(1);
        keyValueDTOList_area.add(keyValue_area_using);

        displayInfoDTO_area.setResults(keyValueDTOList_area);
        resultList.add(displayInfoDTO_area);


        // 设备总数 运行设备总数
        DisplayInfoDTO displayInfoDTO_item = new DisplayInfoDTO();
        List<KeyValueDTO> keyValueDTOList_item = new ArrayList<>();

        KeyValueDTO keyValue_item_total = new KeyValueDTO();
        keyValue_item_total.setKey("设备总数");
        // todo
        keyValue_item_total.setValue(1);
        keyValueDTOList_item.add(keyValue_item_total);

        KeyValueDTO keyValue_item_using = new KeyValueDTO();
        keyValue_item_using.setKey("运行设备总数");
        // todo
        keyValue_item_using.setValue(1);
        keyValueDTOList_item.add(keyValue_item_using);

        displayInfoDTO_area.setResults(keyValueDTOList_item);
        resultList.add(displayInfoDTO_item);

        // 故障报警数量
        DisplayInfoDTO displayInfoDTO_alarm = new DisplayInfoDTO();
        List<KeyValueDTO> keyValueDTOList_alarm = new ArrayList<>();
        KeyValueDTO keyValue_alarm_total = new KeyValueDTO();
        keyValue_alarm_total.setKey("故障报警总数");
        // todo
        keyValue_alarm_total.setValue(1);
        keyValueDTOList_alarm.add(keyValue_alarm_total);

        displayInfoDTO_alarm.setResults(keyValueDTOList_alarm);
        resultList.add(displayInfoDTO_alarm);

        return resultList;
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
    public PageInfoVO getItemInfo(String keyword, String sysCode, String areaCode, Integer pageNumber, Integer pageSize) {
        List<String> areaCodeList = null;
        if(StringUtils.isNotBlank(areaCode)){
            areaCodeList =  adminAPI.getAllChildBuildingAreaCodeList(areaCode).getData();
        }
        PageInfo<TblItem> tblItemPageInfo = itemAPI.searchItemBySysCodeAndTypeCodeAndAreaCodeListAndKeywordPage(sysCode, null, keyword, areaCodeList, pageNumber, pageSize).getData();
        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(tblItemPageInfo);

        List<Long> itemIds = tblItemPageInfo.getList().stream().map(TblItem::getId).collect(Collectors.toList());

        // 获取所有设备的点位信息
        List<TblItemParameter> itemParameterList = itemAPI.getParameterListByItemIds(itemIds).getBody().getData();


        List<ItemInfoOfBroadcastDTO> resultList = new ArrayList<>();
        // 获取设备基本信息
        for (TblItem item : tblItemPageInfo.getList()) {
            ItemInfoOfBroadcastDTO itemInfoOfBroadcastDTO = new ItemInfoOfBroadcastDTO();
            itemInfoOfBroadcastDTO.setItemId(item.getId());
            itemInfoOfBroadcastDTO.setItemCode(item.getCode());
            itemInfoOfBroadcastDTO.setItemName(item.getName());
            itemInfoOfBroadcastDTO.setAreaCode(item.getAreaCode());
            itemInfoOfBroadcastDTO.setAreaName(redisOperationService.getBuildAreaNameByCode(item.getAreaCode()));
            // todo 怎么获取IP地址存疑
            itemInfoOfBroadcastDTO.setIpAddress("10.10.82.146");
            // 获取该设备的参数
            itemParameterList.forEach(e->{
                if(e.getItemCode().equals(item.getCode())){
                    if(e.getParameterType().equals("OnlineState")){
                        itemInfoOfBroadcastDTO.setOnlineParameterCode(e.getCode());
                    }
                    // todo 替换OnlineState
                    if(e.getParameterType().equals("OnlineState")){
                        itemInfoOfBroadcastDTO.setOnlineParameterCode(e.getCode());
                    }
                    // todo 替换OnlineState
                    if(e.getParameterType().equals("OnlineState")){
                        itemInfoOfBroadcastDTO.setOnlineParameterCode(e.getCode());
                    }
                    // todo 替换OnlineState
                    if(e.getParameterType().equals("OnlineState")){
                        itemInfoOfBroadcastDTO.setOnlineParameterCode(e.getCode());
                    }
                }
            });

            // 获取报警信息
            TblAlarmRecordUnhandle alarmRecordUnhandle = alarmAPI.getAlarmInfoByItemCodeLimitOne(item.getCode()).getData();
            if(null != alarmRecordUnhandle && alarmRecordUnhandle.getAlarmCategory() == 1){
                itemInfoOfBroadcastDTO.setAlarmStatus("故障报警");
            }
            if(null != alarmRecordUnhandle && alarmRecordUnhandle.getAlarmCategory() == 0){
                itemInfoOfBroadcastDTO.setAlarmStatus("监测报警");
            }

            resultList.add(itemInfoOfBroadcastDTO);
        }
        pageInfoVO.setList(resultList);
        return pageInfoVO;
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
    public PageInfoVO getAlarmInfo(String keyword, String sysCode, Integer pageNumber, Integer pageSize) {
        PageInfo<TblAlarmRecordUnhandle> tblAlarmRecordUnhandlePageInfo = alarmAPI.getAlarmInfoBySysCodeLimitOneByKeywordPage(keyword, sysCode, pageNumber, pageSize).getData();

        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(tblAlarmRecordUnhandlePageInfo);

        List<String> itemCodeList = tblAlarmRecordUnhandlePageInfo.getList().stream().map(TblAlarmRecordUnhandle::getItemCode).collect(Collectors.toList());

        List<TblItemDTO> items = itemAPI.searchItemByItemCodes(itemCodeList).getData();
        List<AlarmInfoOfBroadcastDTO> resultList = new ArrayList<>();

        // todo 怎么获取IP地址存疑
        for (TblAlarmRecordUnhandle alarm : tblAlarmRecordUnhandlePageInfo.getList()) {
            AlarmInfoOfBroadcastDTO alarmInfoOfBroadcastDTO = new AlarmInfoOfBroadcastDTO();
            alarmInfoOfBroadcastDTO.setAlarmId(alarm.getId());
            alarmInfoOfBroadcastDTO.setAlarmCategory(alarm.getAlarmCategory());
            alarmInfoOfBroadcastDTO.setAlarmLevel(alarm.getAlarmLevel());
            alarmInfoOfBroadcastDTO.setAlarmTime(alarm.getAlarmTime());

            for (TblItemDTO tblItemDTO : items) {
                if(tblItemDTO.getCode().equals(alarm.getItemCode())){
                    alarmInfoOfBroadcastDTO.setItemId(tblItemDTO.getId());
                    alarmInfoOfBroadcastDTO.setItemCode(tblItemDTO.getCode());
                    alarmInfoOfBroadcastDTO.setItemName(tblItemDTO.getName());
                    alarmInfoOfBroadcastDTO.setAreaCode(tblItemDTO.getAreaCode());
                    alarmInfoOfBroadcastDTO.setAreaName(redisOperationService.getBuildAreaNameByCode(tblItemDTO.getAreaCode()));
                }
            }
            resultList.add(alarmInfoOfBroadcastDTO);
        }
        pageInfoVO.setList(resultList);

        return pageInfoVO;
    }
}
