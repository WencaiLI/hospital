package com.thtf.environment.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.github.pagehelper.PageInfo;
import com.thtf.common.constant.AlarmConstants;
import com.thtf.common.constant.ItemConstants;
import com.thtf.common.dto.alarmserver.ListAlarmInfoLimitOneParamDTO;
import com.thtf.common.dto.itemserver.*;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.feign.AdminAPI;
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
    private AdminAPI adminAPI;

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
        List<String> buildingCodeList = StringUtils.isNotBlank(buildingCodes)?Arrays.asList(buildingCodes.split(",")):adminAPI.listBuildingCodeUserSelf().getData();
        List<String> areaCodeList = StringUtils.isNotBlank(areaCode)?Arrays.asList(areaCode.split(",")):null;
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
     * @Description:
     * @Date: 2022/10/7
     * @Param keyword: 关键词
     * @Param sysCode: 子系统编码
     * @Param areaCode: 区域编码
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: com.thtf.environment.dto.PageInfoVO
     */
    @Override
    public PageInfo<ItemInfoOfBroadcastDTO> getItemInfo(String sysCode, String buildingCodes, String areaCode, String runVale, String keyword, Integer pageNumber, Integer pageSize) {
        PageInfo<ItemInfoOfBroadcastDTO> pageInfoVO = new PageInfo<>();
        List<String> buildingCodeList = StringUtils.isNotBlank(buildingCodes) ? Arrays.asList(buildingCodes.split(",")) : adminAPI.listBuildingCodeUserSelf().getData();
        List<String> areaCodeList = StringUtils.isNotBlank(areaCode) ? Arrays.asList(areaCode.split(",")) : null;
        ListItemNestedParametersPageParamDTO listItemNestedParametersPageParam = new ListItemNestedParametersPageParamDTO();
        listItemNestedParametersPageParam.setSysCode(sysCode);
        listItemNestedParametersPageParam.setBuildingCodeList(buildingCodeList);
        listItemNestedParametersPageParam.setAreaCodeList(areaCodeList);
        listItemNestedParametersPageParam.setPageNumber(pageNumber);
        listItemNestedParametersPageParam.setPageSize(pageSize);
        if(StringUtils.isNotBlank(keyword)){
            listItemNestedParametersPageParam.setKeyword(keyword);
            listItemNestedParametersPageParam.setCodeKey(keyword);
            listItemNestedParametersPageParam.setAreaKey(keyword);
            listItemNestedParametersPageParam.setNameKey(keyword);
        }
        // 运行状态筛选
        if (null != runVale && runVale.equals("1")) {
            String parameterValue = commonService.getParameterValueByStateExplain(sysCode, itemParameterConfig.getBroadcastOnline(), null, new String[]{"在线", "运行", "行", "上"});
            List<ParameterTypeCodeAndValueDTO> parameterList = new ArrayList<>();
            ParameterTypeCodeAndValueDTO paramTypeCodeAndValueDTO = new ParameterTypeCodeAndValueDTO();
            paramTypeCodeAndValueDTO.setParameterTypeCode(itemParameterConfig.getBroadcastOnline());
            paramTypeCodeAndValueDTO.setParameterValue(parameterValue);
            parameterList.add(paramTypeCodeAndValueDTO);
            listItemNestedParametersPageParam.setParameterList(parameterList);
        }

        PageInfo<ItemNestedParameterVO> itemPageInfo = itemAPI.listItemNestedParametersPage(listItemNestedParametersPageParam).getData();

        BeanUtils.copyProperties(itemPageInfo,pageInfoVO);
        if(CollectionUtils.isEmpty(itemPageInfo.getList())){
            return pageInfoVO;
        }
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
            itemInfoOfBroadcastDTO.setParameterList(parameterConverter.toParameterInfoList(item.getParameterList()));
            resultList.add(itemInfoOfBroadcastDTO);
        }
        pageInfoVO.setList(resultList);
        return pageInfoVO;
    }

    /**
     * @Author: liwencai
     * @Description: 获取故障信息
     * @Date: 2022/10/7
     * @Param keyword: 关键词
     * @Param sysCode: 子系统编码
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: com.thtf.environment.dto.PageInfoVO
     */
    @Override
    public PageInfo<AlarmInfoOfBroadcastDTO> getAlarmInfo(String keyword, String sysCode, String buildingCodes, String areaCode, Integer pageNumber, Integer pageSize) {
        PageInfo<AlarmInfoOfBroadcastDTO> pageInfoVO = new PageInfo<>();
        List<String> buildingCodeList = StringUtils.isNotBlank(buildingCodes)?Arrays.asList(buildingCodes.split(",")):adminAPI.listBuildingCodeUserSelf().getData();;
        List<String> areaCodeList = StringUtils.isNotBlank(areaCode)?Arrays.asList(areaCode.split(",")):null;
        TblItem tblItem = new TblItem();
        tblItem.setBuildingCodeList(buildingCodeList);
        tblItem.setAreaCodeList(areaCodeList);
        tblItem.setSystemCode(sysCode);
        tblItem.setFault(ItemConstants.ITEM_FAULT_TRUE);
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
                    alarmInfoOfBroadcastDTO.setBuildingCode(tblItemDTO.getBuildingCode());
                    alarmInfoOfBroadcastDTO.setStayTime(commonService.getAlarmStayTime(alarmInfoOfBroadcastDTO.getAlarmTime()));
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
     * @Param sysCode: 子系统编码
     * @Param itemCodes: 设备编码集
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
        innerResult.setParameterList(parameterConverter.toParameterInfoList(itemNestedParameterVO.getParameterList()));
        return innerResult;
    }
}
