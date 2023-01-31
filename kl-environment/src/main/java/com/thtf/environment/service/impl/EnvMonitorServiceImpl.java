package com.thtf.environment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.adminserver.AreaNestBuildingDTO;
import com.thtf.common.dto.alarmserver.EChartsHourlyVO;
import com.thtf.common.dto.alarmserver.ItemAlarmInfoDTO;
import com.thtf.common.dto.alarmserver.ListAlarmInfoLimitOneParamDTO;
import com.thtf.common.dto.alarmserver.TwentyFourHourAlarmStatisticsDTO;
import com.thtf.common.dto.itemserver.*;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblGroup;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.common.util.ArithUtil;
import com.thtf.environment.common.Constant.ParameterConstant;
import com.thtf.environment.config.ParameterConfigNacos;
import com.thtf.environment.dto.*;
import com.thtf.environment.dto.convert.ItemTypeConvert;
import com.thtf.environment.dto.convert.PageInfoConvert;
import com.thtf.environment.dto.convert.ParameterConverter;
import com.thtf.environment.entity.TblHistoryMoment;
import com.thtf.environment.mapper.TblHistoryMomentMapper;
import com.thtf.environment.service.EnvMonitorService;
import com.thtf.environment.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.hint.HintManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Author: liwencai
 * @Date: 2022/10/25 14:28
 * @Description:
 */
@Service
public class EnvMonitorServiceImpl extends ServiceImpl<TblHistoryMomentMapper, TblHistoryMoment> implements EnvMonitorService {

    @Autowired
    private TblHistoryMomentMapper tblHistoryMomentMapper;

    @Resource
    private ParameterConfigNacos parameterConfigNacos;

    @Resource
    private ItemAPI itemAPI;

    @Resource
    private AlarmAPI alarmAPI;

    @Resource
    private AdminAPI adminAPI;

    @Resource
    private ItemTypeConvert itemTypeConvert;

    @Resource
    private PageInfoConvert pageInfoConvert;

    @Resource
    private ParameterConverter itemParameterConvert;

    private final static String TBL_HISTORY_MOMENT = "tbl_history_moment";
    private final static String DAY_START_SUFFIX = " 00:00:00";
    private final static String DAY_END_SUFFIX = " 23:59:59";
    private final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    /**
     * @Author: liwencai
     * @Description: 获取前端展示数据
     * @Date: 2022/10/25
     * @Param: sysCode: 子系统编码
     * @Return: com.thtf.environment.vo.EnvMonitorDisplayVO
     */
    @Override
    public ItemTotalAndOnlineAndAlarmNumDTO getDisplayInfo(String sysCode, String areaCode,String buildingCodes) {
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        String itemTypeCodes = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).distinct().collect(Collectors.joining(","));

        return itemAPI.getItemOnlineAndTotalAndAlarmItemNumber(sysCode,areaCode,buildingCodes,itemTypeCodes,ParameterConstant.ENV_MONITOR_ONLINE,ParameterConstant.ENV_MONITOR_ONLINE_VALUE,true,true).getData();
    }
    /**
     * @Author: liwencai
     * @Description: 以24小时为维度统计报警总数,每日的每小时累加
     * @Date: 2023/1/11
     * @Param sysCode: 子系统编码
     * @Param buildingCodes: 建筑编码集
     * @Param isHandled: 是否是已处理
     * @Param areaCode: 区域编码
     * @Param startTime: 开始时间
     * @Param endTime: 结束时间
     * @Return: com.thtf.common.response.JsonResult
     */
    @Override
    public EChartsMoreVO getTotalAlarmHourly(String sysCode, String buildingCodes, String areaCode, Boolean isHandled, String startTime, String endTime) {
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        if(null == parameterInfo || parameterInfo.size() == 0){
            return null;
        }
        EChartsMoreVO result = new EChartsMoreVO();
        List<KeyValueDTO> values = new ArrayList<>();
        Map<String, String> codeNameMap = new HashMap<>();
        parameterInfo.forEach(e->{
            codeNameMap.put(e.getItemTypeCode(),e.getItemTypeName().split("[(]")[0].split("（")[0]);
        });
        List<String> itemTypeCodeList = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).distinct().collect(Collectors.toList());
        // 计算未处理的24小时统计
        TwentyFourHourAlarmStatisticsDTO param = new TwentyFourHourAlarmStatisticsDTO();
        param.setSysCode(sysCode);
        if(StringUtils.isNotBlank(buildingCodes)){
            param.setBuildingCodeList(Arrays.asList(buildingCodes.split(",")));
        }else {
            if(StringUtils.isNotBlank(areaCode)){
                param.setAreaCode(areaCode);
            }
        }
        if(itemTypeCodeList.size()>0){
            param.setItemTypeCodeList(itemTypeCodeList);
        }
        param.setStartTime(startTime);
        param.setEndTime(endTime);
        param.setIsHandled(isHandled);
        EChartsHourlyVO data = alarmAPI.getTwentyFourHourAlarmStatistics(param).getData();
        itemTypeCodeList.forEach(itemTypeCode->{
            KeyValueDTO keyValueDTO = new KeyValueDTO();
            String property = null;
            try {
                property = this.getParameterName(itemTypeCode,parameterInfo).split("[(]")[0].split("（")[0];
                // property = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(itemType.getCode()).getParameterTypeName();
            }catch (Exception ignored){
            }
            keyValueDTO.setKey(property);
            //
            List<Long> list = data.getValues().get(itemTypeCode);
            keyValueDTO.setValue(list);
            values.add(keyValueDTO);
        });
        result.setKeys(data.getKeys());
        result.setValues(values);
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 未处理报警数据统计
     * @Date: 2022/10/25
     * @Param: startTime: 开始时间
     * @Param: endTime: 结束时间
     * @Param: buildingCodes: 建筑编码集
     * @Param: areaCode: 区域编码
     * @Return: com.thtf.environment.vo.EChartsVO
     */
    @Override
    public EChartsVO getAlarmUnhandledStatistics(String sysCode,String buildingCodes, String areaCode,Boolean isHandled, String startTime, String endTime) {
        // 获取所有的数据统计
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        if(null == parameterInfo || parameterInfo.size() == 0){
            return null;
        }
        // 在nacos 中配置的设备类别编码
        List<String> itemTypeList = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).collect(Collectors.toList());
        EChartsVO result = new EChartsVO();
        // 没传时间默认当天
        if(StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)){
            Map<String, String> todayStartTimeAndEndTimeString = getTodayStartTimeAndEndTimeString();
            startTime = todayStartTimeAndEndTimeString.get("startTime");
            endTime = todayStartTimeAndEndTimeString.get("endTime");
        }
        // 获取所有设备类别
        List<String> itemTypeCodeList = this.getItemTypeList(sysCode).stream().map(CodeNameVO::getCode).collect(Collectors.toList());
        // 初始化数据
        List<ItemAlarmInfoDTO> initList = new ArrayList<>();
        for (String itemTypeCode : itemTypeCodeList) {
            ItemAlarmInfoDTO innerResult = new ItemAlarmInfoDTO();
            innerResult.setAttribute(itemTypeCode);
            innerResult.setMalfunctionAlarmNumber(0);
            innerResult.setMonitorAlarmNumber(0);
            innerResult.setItemNumber(0);
            initList.add(innerResult);
        }
        // 查询在数据库中的数据
        List<ItemAlarmInfoDTO> dataInDB = alarmAPI.getItemTypeAlarmSituation(sysCode, areaCode, buildingCodes, null, null,isHandled, startTime, endTime).getData();
        // 替换初始化数据
        initList.forEach(e->{
            for (ItemAlarmInfoDTO itemAlarmInfoDTO : dataInDB) {
                if(e.getAttribute().equals(itemAlarmInfoDTO.getAttribute())){
                    e.setMonitorAlarmNumber(itemAlarmInfoDTO.getMonitorAlarmNumber());
                    e.setMalfunctionAlarmNumber(itemAlarmInfoDTO.getMalfunctionAlarmNumber());
                    e.setItemNumber(e.getMalfunctionAlarmNumber()+e.getMonitorAlarmNumber());
                }
            }
        });
        List<String> keys = new ArrayList<>();
        initList = initList.stream().filter(e -> itemTypeList.contains(e.getAttribute())).collect(Collectors.toList());
        List<String> collect = initList.stream().map(ItemAlarmInfoDTO::getAttribute).map(Object::toString).collect(Collectors.toList());
        collect.forEach(e->{
            List<ParameterTemplateAndDetailDTO> collect1 = parameterInfo.stream().filter(item -> item.getItemTypeCode().equals(e)).collect(Collectors.toList());
            keys.add(collect1.get(0).getName().split("（")[0].split("[(]")[0]);
        });
        result.setKeys(keys);
        result.setValues(initList.stream().map(ItemAlarmInfoDTO::getItemNumber).collect(Collectors.toList()));
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备类别编码和名称集
     * @Date: 2022/10/25
     * @Param: sysCode: 子系统编码
     * @Return: java.util.List<com.thtf.environment.vo.CodeNameVO>
     */
    @Override
    public List<CodeNameVO> getItemTypeList(String sysCode) {
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        if(null == parameterInfo || parameterInfo.size() == 0){
            return null;
        }
        List<String> collect = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).collect(Collectors.toList());

        List<CodeNameVO> codeNameVOS = itemTypeConvert.toCodeNameVO(itemAPI.getItemTypesBySysId(sysCode).getBody().getData());
        codeNameVOS.removeIf(e->!collect.contains(e.getCode()));
        return codeNameVOS;
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/24
     * @Param paramVO:
     * @return: com.thtf.environment.dto.PageInfoVO
     */
    @Override
    public PageInfoVO listItemInfo(EnvMonitorItemParamVO paramVO) {
        // 查询故障或报警
        if(null == paramVO.getAlarmCategory()){
            return this.listAllItem(paramVO);
        }else { // 报警设备
            return this.listAlarmItem(paramVO);
        }
    }

    /**
     * @Author: liwencai
     * @Description: 获取报警设备信息
     * @Date: 2022/11/24
     * @Param paramVO:
     * @return: com.thtf.environment.dto.PageInfoVO
     */
    public PageInfoVO listAlarmItem(EnvMonitorItemParamVO paramVO){

        // 查询指定的监测设备类型
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        if(null == parameterInfo || parameterInfo.size() == 0){
            return null;
        }
        List<String> itemTypeCodeList = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).collect(Collectors.toList());
        // 查询处于报警或故障的设备编码
        List<String> buildingCodeList = null;
        List<String> areaCodeList = null;
        if(StringUtils.isNotBlank(paramVO.getBuildingCodes())){
            buildingCodeList = Arrays.asList(paramVO.getBuildingCodes().split(","));
        }else {
            if(StringUtils.isNotBlank(paramVO.getAreaCode())){
                areaCodeList = Arrays.asList(paramVO.getAreaCode().split(","));
            }
        }
        TblItem tblItem = new TblItem();
        tblItem.setBuildingCodeList(buildingCodeList);
        tblItem.setAreaCodeList(areaCodeList);
        tblItem.setItemTypeCodeList(itemTypeCodeList);
        tblItem.setSystemCode(paramVO.getSysCode());
        // 对设备名称、编码、区域名称进行模糊查询
        tblItem.setKeyword(paramVO.getKeyword());
        tblItem.setKeyName(paramVO.getKeyword());
        tblItem.setKeyCode(paramVO.getKeyword());
        tblItem.setKeyAreaName(paramVO.getKeyword());

        if(StringUtils.isNotBlank(paramVO.getItemTypeCode())){
            tblItem.setTypeCode(paramVO.getItemTypeCode());
        }
        if(null != paramVO.getAlarmCategory()){
            if(paramVO.getAlarmCategory() == 1){
                tblItem.setAlarm(0);
                tblItem.setFault(1);
            }
            if(paramVO.getAlarmCategory() == 0){
                tblItem.setAlarm(1);
            }
        }
        PageInfo<TblItem> itemData = itemAPI.queryAllItemsPage(tblItem).getData();
        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(itemData);

        List<TblItem> itemList = itemData.getList();
        if(itemList ==  null || itemList.size() == 0){
            return null;
        }
        List<String> itemCodeList = itemList.stream().map(TblItem::getCode).collect(Collectors.toList());

        // 查询报警信息
        List<TblAlarmRecordUnhandle> alarmRecordList = alarmAPI.getAlarmInfoByItemCodeListAndCategoryLimitOne(itemCodeList, paramVO.getAlarmCategory()).getData();
        ListAlarmInfoLimitOneParamDTO listAlarmInfoLimitOneParamDTO = new ListAlarmInfoLimitOneParamDTO();
        listAlarmInfoLimitOneParamDTO.setSystemCode(paramVO.getSysCode());
        if(null != paramVO.getAlarmCategory()){
            listAlarmInfoLimitOneParamDTO.setAlarmCategory(String.valueOf(paramVO.getAlarmCategory()));
        }

        listAlarmInfoLimitOneParamDTO.setItemCodeList(itemCodeList);
        listAlarmInfoLimitOneParamDTO.setKeyword(paramVO.getKeyword());
        listAlarmInfoLimitOneParamDTO.setKeywordOfItemName(paramVO.getKeyword());
        listAlarmInfoLimitOneParamDTO.setKeywordOfItemCode(paramVO.getKeyword());
        listAlarmInfoLimitOneParamDTO.setKeywordOfAlarmDesc(paramVO.getKeyword());
//        listAlarmInfoLimitOneParamDTO.setPageNumber(paramVO.getPageNumber());
//        listAlarmInfoLimitOneParamDTO.setPageSize(paramVO.getPageSize());

//        PageInfo<TblAlarmRecordUnhandle> tblAlarmRecordUnhandlePageInfo = alarmAPI.listAlarmInfoLimitOnePage(listAlarmInfoLimitOneParamDTO).getData();
//        // PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(tblAlarmRecordUnhandlePageInfo);
//        List<String> collect = tblAlarmRecordUnhandlePageInfo.getList().stream().map(TblAlarmRecordUnhandle::getItemCode).collect(Collectors.toList());
//        itemList.removeIf(e->!collect.contains(e.getCode()));
        List<EnvMonitorItemResultVO> resultVOList = new ArrayList<>();
//        List<String> itemCodeList;

//        PageInfo<TblAlarmRecordUnhandle> pageInfo = alarmAPI.getAlarmInfoBySysCodeAndCategoryLimitOneByKeywordPage(paramVO.getKeyword(), paramVO.getAlarmCategory(), paramVO.getSysCode(), paramVO.getPageNumber(), paramVO.getPageSize()).getData();
//        // 设备编码集
//        itemCodeList = pageInfo.getList().stream().map(TblAlarmRecordUnhandle::getItemCode).collect(Collectors.toList());
//        // 设备参数集
        List<TblItemParameter> itemParameterList = itemAPI.searchParameterByItemCodes(itemCodeList).getData();
//        // 设备信息集
//        List<TblItem> itemList = itemAPI.searchItemByItemCodeList(itemCodeList).getData();
//        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(pageInfo);
//        // 分组id集
        List<String> groupIdStringList;
        groupIdStringList = itemList.stream().filter(e->null != e.getGroupId()).map(TblItem::getGroupId).map(String::valueOf).collect(Collectors.toList());
        List<TblGroup> groupList = null;
        if(groupIdStringList.size()>0){
            groupList = itemAPI.searchGroupByIdList(groupIdStringList).getData();
        }
        for (TblAlarmRecordUnhandle alarmRecord: alarmRecordList) {
            EnvMonitorItemResultVO envMonitorItemResultVO = new EnvMonitorItemResultVO();
            // 匹配设备信息
            List<TblGroup> finalGroupList = groupList;
            itemList.forEach(item->{
                if(item.getCode().equals(alarmRecord.getItemCode())){
                    envMonitorItemResultVO.setItemCode(item.getCode());
                    envMonitorItemResultVO.setItemName(item.getName());
                    envMonitorItemResultVO.setAreaCode(item.getAreaCode());
                    envMonitorItemResultVO.setAreaName(item.getAreaName());
                    envMonitorItemResultVO.setItemTypeCode(item.getTypeCode());
                    envMonitorItemResultVO.setItemTypeName(item.getItemTypeName());
                    envMonitorItemResultVO.setAlarmCategory(alarmRecord.getAlarmCategory());
                    // 匹配设备模型视角信息
                    if(StringUtils.isNotBlank(item.getViewLongitude())){
                        envMonitorItemResultVO.setEye(Arrays.stream(item.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                    }
                    if(StringUtils.isNotBlank(item.getViewLatitude())){
                        envMonitorItemResultVO.setCenter(Arrays.stream(item.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                    }
                    // 匹配分组信息
                    if(null != finalGroupList){
                        finalGroupList.forEach(e->{
                            if(e.getId().equals(item.getGroupId())){
                                envMonitorItemResultVO.setGroupId(e.getId());
                                envMonitorItemResultVO.setGroupName(e.getName());
                            }
                        });
                    }
                }
            });
            this.convertToParameter(envMonitorItemResultVO,itemParameterList,parameterInfo,itemTypeCodeList);
            resultVOList.add(envMonitorItemResultVO);
        }
        pageInfoVO.setList(resultVOList);
        return pageInfoVO;
    }

    void convertToParameter(EnvMonitorItemResultVO envMonitorItemResultVO,List<TblItemParameter> itemParameterList,List<ParameterTemplateAndDetailDTO>  parameterInfo,List<String> itemTypeCodeList){
        // 匹配参数
        itemParameterList.forEach(parameter->{
            if(parameter.getItemCode().equals(envMonitorItemResultVO.getItemCode())){
                // 在线状态
                if(ParameterConstant.ENV_MONITOR_ONLINE.equals(parameter.getParameterType())){
                    envMonitorItemResultVO.setOnlineParameterCode(parameter.getCode());
                    if(null != parameter.getValue()){
                        envMonitorItemResultVO.setOnlineParameterValue(Optional.ofNullable(parameter.getValue()).orElse("")+Optional.ofNullable(parameter.getUnit()).orElse(""));
                    }
                }
                // 故障
                if(ParameterConstant.ENV_MONITOR_FAULT.equals(parameter.getParameterType())){
                    envMonitorItemResultVO.setFaultParameterCode(parameter.getCode());
                    if(null != parameter.getValue()){
                        envMonitorItemResultVO.setFaultParameterValue(Optional.ofNullable(parameter.getValue()).orElse("")+Optional.ofNullable(parameter.getUnit()).orElse(""));
                    }
                }
                // 报警
                if(ParameterConstant.ENV_MONITOR_ALARM.equals(parameter.getParameterType())){
                    envMonitorItemResultVO.setAlarmParameterCode(parameter.getCode());
                    if(null != parameter.getValue()){
                        envMonitorItemResultVO.setAlarmParameterValue(Optional.ofNullable(parameter.getValue()).orElse("")+Optional.ofNullable(parameter.getUnit()).orElse(""));
                    }
                }
                // 监测值
                if(parameter.getParameterType().equals(this.getParameterType(envMonitorItemResultVO.getItemTypeCode(),parameterInfo))){
                    if(null != parameter.getValue()){
                        envMonitorItemResultVO.setDataCollectionValue(Optional.ofNullable(parameter.getValue()).orElse("")+Optional.ofNullable(parameter.getUnit()).orElse(""));
                    }
                    envMonitorItemResultVO.setDataCollectionTime(parameter.getDataUpdateTime() == null?parameter.getCreatedTime():parameter.getDataUpdateTime());
                }
            }

        });
    }




    /**
     * @Author: liwencai
     * @Description: 查询所有的设备详情（分页）
     * @Date: 2022/11/24
     * @Param paramVO:
     * @return: com.thtf.environment.dto.PageInfoVO
     */
    public PageInfoVO listAllItem(EnvMonitorItemParamVO paramVO){
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        if(null == parameterInfo || parameterInfo.size() == 0){
            return null;
        }
        // 所有设备类别编码
        List<String> itemTypeCodeList = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).collect(Collectors.toList());

        List<EnvMonitorItemResultVO> resultVOList = new ArrayList<>();
        List<String> itemCodeList;
        List<String> groupIdStringList;
        // todo liwencai 需要修改
        PageInfo<TblItem> pageInfo = itemAPI.searchItemBySysCodeAndTypeCodeAndAreaCodeListAndKeywordPage(paramVO.getSysCode(), paramVO.getItemTypeCode(), paramVO.getKeyword(), null, paramVO.getPageNumber(), paramVO.getPageSize()).getData();
        // 设备编码集
        itemCodeList = pageInfo.getList().stream().map(TblItem::getCode).collect(Collectors.toList());
        // 查询设备参数集
        List<TblItemParameter> itemParameterList = itemAPI.searchParameterByItemCodes(itemCodeList).getData();
        // 查询设备的最新报警信息
        List<TblAlarmRecordUnhandle> alarmList = alarmAPI.getAlarmInfoByItemCodeListLimitOne(itemCodeList).getData();
        // 分组id集
        groupIdStringList = pageInfo.getList().stream().filter(e->null != e.getGroupId()).map(TblItem::getGroupId).map(String::valueOf).collect(Collectors.toList());
        List<TblGroup> groupList = null;
        if(groupIdStringList.size()>0){
            groupList = itemAPI.searchGroupByIdList(groupIdStringList).getData();
        }
        // 匹配分组信息集
        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(pageInfo);
        for (TblItem item : pageInfo.getList()) {
            EnvMonitorItemResultVO envMonitorItemResultVO = new EnvMonitorItemResultVO();
            envMonitorItemResultVO.setItemCode(item.getCode());
            envMonitorItemResultVO.setItemName(item.getName());
            envMonitorItemResultVO.setAreaCode(item.getAreaCode());
            envMonitorItemResultVO.setAreaName(item.getAreaName());
            // 匹配分组信息
            if(null != groupList){
                groupList.forEach(e->{
                    if(e.getId().equals(item.getGroupId())){
                        envMonitorItemResultVO.setGroupId(e.getId());
                        envMonitorItemResultVO.setGroupName(e.getName());
                    }
                });
            }
            // 匹配模型视角信息
            if(StringUtils.isNotBlank(item.getViewLongitude())){
                envMonitorItemResultVO.setEye(Arrays.stream(item.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            if(StringUtils.isNotBlank(item.getViewLatitude())){
                envMonitorItemResultVO.setCenter(Arrays.stream(item.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            // 匹配参数信息
            this.convertToParameter(envMonitorItemResultVO,itemParameterList,parameterInfo,itemTypeCodeList);

            // 匹配报警信息
            alarmList.forEach(e->{
                if(e.getItemCode().equals(item.getCode())){
                    envMonitorItemResultVO.setAlarmCategory(e.getAlarmCategory());
                }
            });
            resultVOList.add(envMonitorItemResultVO);
        }
        pageInfoVO.setList(resultVOList);
        return pageInfoVO;
    }

    /**
     * @Author: liwencai
     * @Description: 查询设备参数
     * @Date: 2022/10/27
     * @Param: itemCode: 设备编码
     * @Return: java.util.List<com.thtf.environment.vo.ItemParameterInfoVO>
     */
    @Override
    public List<ItemParameterInfoVO> listParameter(String itemCode) {
        return itemParameterConvert.toItemParameterInfoVOList(itemAPI.searchParameterByItemCodes(Collections.singletonList(itemCode)).getData());
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/10/27
     * @Param: itemCode:
     * @Param: itemTypeCode:
     * @Param: parameterCode:
     * @Param: date:
     * @Return: com.thtf.environment.vo.EChartsVO
     */
    @Override
    public EChartsVO getHourlyHistoryMoment(String itemCode,String itemTypeCode, String parameterCode, String date) {
        EChartsVO result = new EChartsVO();
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        if(null == parameterInfo){
            return null;
        }

        parameterInfo.forEach(e->{
            if (e.getItemTypeCode().equals(itemTypeCode)){
                result.setUnit(e.getUnit());
            }
        });
        List<TimeValueDTO> hourlyHistoryMoment = null;
        if(StringUtils.isBlank(parameterCode)){
            if(StringUtils.isNotBlank(itemTypeCode)){
                // String parameterType = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(itemTypeCode).getParameterType();
                String parameterType = this.getParameterType(itemTypeCode,parameterInfo);
                parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
            }else if(StringUtils.isNotBlank(itemCode)){
                TblItem tblItem = new TblItem();
                tblItem.setCode(itemCode);
                List<TblItem> data = itemAPI.queryAllItems(tblItem).getData();
                if(null != data && data.size()>0){
                    // String parameterType = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(data.get(0).getTypeCode()).getParameterType();
                    String parameterType = this.getParameterType(itemTypeCode,parameterInfo);
                    parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
                }
            }
        }
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.addTableShardingValue(TBL_HISTORY_MOMENT,date+DAY_START_SUFFIX);
            hourlyHistoryMoment = tblHistoryMomentMapper.getHourlyHistoryMoment(parameterCode,date+DAY_START_SUFFIX,date+DAY_END_SUFFIX);
        }catch (Exception e){
            // hourlyHistoryMoment = null;
        }
        List<Integer> collect = new ArrayList<>();
        if (null != hourlyHistoryMoment){
            hourlyHistoryMoment.forEach(timeValueDTO->{
                timeValueDTO.setTime(String.format("%02d", Integer.valueOf(timeValueDTO.getTime())));
            });
            collect = hourlyHistoryMoment.stream().map(TimeValueDTO::getTime).map(Integer::valueOf).collect(Collectors.toList());
        }else {
            hourlyHistoryMoment = new ArrayList<>();
        }
        for (int i = 0; i < 24; i++) {
            if(!collect.contains(i)){
                TimeValueDTO timeValueDTO = new TimeValueDTO();
                timeValueDTO.setTime(String.format("%02d", i));
                timeValueDTO.setValue(0);
                hourlyHistoryMoment.add(timeValueDTO);
            }
        }
        hourlyHistoryMoment.sort(Comparator.comparing(TimeValueDTO::getTime));

        result.setKeys(hourlyHistoryMoment.stream().map(TimeValueDTO::getTime).collect(Collectors.toList()));
        result.setValues(hourlyHistoryMoment.stream().map(TimeValueDTO::getValue).collect(Collectors.toList()));
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 获取指定月的每日历史数据
     * @Date: 2022/11/26
     * @Param itemCode: 设备编码
     * @Param itemTypeCode: 设备类别编码
     * @Param parameterCode: 设备参数编码
     * @Param date: 日期
     * @return: com.thtf.environment.vo.EChartsVO
     */
    @Override
    public EChartsVO getDailyHistoryMoment(String itemCode, String itemTypeCode, String parameterCode, String date) {
        EChartsVO result = new EChartsVO();
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        Date newDate = YYMMDDStringToDate(date);
        if(null == date){
            log.error("时间格式错误");
            return null;
        }
        parameterInfo.forEach(e->{
            if (e.getItemTypeCode().equals(itemTypeCode)){
                result.setUnit(e.getUnit());
            }
        });
        List<TimeValueDTO> hourlyHistoryMoment = null;
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.addTableShardingValue(TBL_HISTORY_MOMENT,date+DAY_END_SUFFIX);
            if(StringUtils.isBlank(parameterCode)){
                if(StringUtils.isNotBlank(itemTypeCode)){
//                    System.out.println(itemTypeCode);
//                    String parameterType = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(itemTypeCode).getParameterType();
                    String parameterType = this.getParameterType(itemTypeCode,parameterInfo);
                    parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
                }else if(StringUtils.isNotBlank(itemCode)){
                    TblItem tblItem = new TblItem();
                    tblItem.setCode(itemCode);
                    List<TblItem> data = itemAPI.queryAllItems(tblItem).getData();
                    if(null != data && data.size()>0){
                        String parameterType = this.getParameterType(itemTypeCode,parameterInfo);
//                        String parameterType = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(data.get(0).getTypeCode()).getParameterType();
                        parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
                    }
                }
            }
            try {
                hourlyHistoryMoment = tblHistoryMomentMapper.getDailyHistoryMoment(parameterCode, getMonthStartAndEndTimeDayString(newDate).get("startTime"), getMonthStartAndEndTimeDayString(newDate).get("endTime"));
            }catch (Exception ignored){
            }
        }
        List<Integer> collect = new ArrayList<>();
        if(hourlyHistoryMoment != null){
            collect = hourlyHistoryMoment.stream().map(TimeValueDTO::getTime).map(Integer::valueOf).collect(Collectors.toList());
        }else {
            hourlyHistoryMoment = new ArrayList<>();
        }
        // 填充数据
        int year = getYear(newDate);
        int month = getMonth(newDate);
        int daysOfMonth = getDaysOfMonth(newDate);
         System.out.println(hourlyHistoryMoment);
        String timePrefix = year+"-"+month+"-";
        // 为null补0
        hourlyHistoryMoment.forEach(e->{
            e.setTime(timePrefix+String.format("%02d", Integer.valueOf(e.getTime())));
        });
        for (int i = 1; i <= daysOfMonth; i++) {
            if(!collect.contains(i)){
                TimeValueDTO timeValueDTO = new TimeValueDTO();
                timeValueDTO.setTime(timePrefix+String.format("%02d", i));
                timeValueDTO.setValue(0);
                hourlyHistoryMoment.add(timeValueDTO);
            }
        }
        // 排序
        hourlyHistoryMoment.sort(Comparator.comparing(TimeValueDTO::getTime));

        result.setKeys(hourlyHistoryMoment.stream().map(TimeValueDTO::getTime).collect(Collectors.toList()));
        result.setValues(hourlyHistoryMoment.stream().map(TimeValueDTO::getValue).collect(Collectors.toList()));
        return result;
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/10/27
     * @Param: itemCode:
     * @Param: itemTypeCode:
     * @Param: parameterCode:
     * @Param: date:
     * @Return: com.thtf.environment.vo.EChartsVO
     */
    @Override
    public EChartsVO getMonthlyHistoryMoment(String itemCode, String itemTypeCode, String parameterCode, String date) {
        EChartsVO result = new EChartsVO();
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        Date newDate = YYMMDDStringToDate(date);
        if(null == date){
            log.error("时间格式错误");
            return null;
        }
        parameterInfo.forEach(e->{
            if (e.getItemTypeCode().equals(itemTypeCode)){
                result.setUnit(e.getUnit());
            }
        });
        List<TimeValueDTO> hourlyHistoryMoment = null;
        try (HintManager hintManager = HintManager.getInstance()) {
            // todo liwencai 此处存在bug
            // 日期是当年的开始时间，至本月时间
            hintManager.addTableShardingValue(TBL_HISTORY_MOMENT,date+DAY_START_SUFFIX);
            if(StringUtils.isBlank(parameterCode)){
                if(StringUtils.isNotBlank(itemTypeCode)){
//                    System.out.println(itemTypeCode);
//                    String parameterType = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(itemTypeCode).getParameterType();
                    String parameterType = this.getParameterType(itemTypeCode,parameterInfo);
                    parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
                }else if(StringUtils.isNotBlank(itemCode)){
                    TblItem tblItem = new TblItem();
                    tblItem.setCode(itemCode);
                    List<TblItem> data = itemAPI.queryAllItems(tblItem).getData();
                    if(null != data && data.size()>0){
//                        String parameterType = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(data.get(0).getTypeCode()).getParameterType();
                        String parameterType = this.getParameterType(itemTypeCode,parameterInfo);
                        parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
                    }
                }
            }
            try {
                // todo liwencai 获取有问题
                hourlyHistoryMoment = tblHistoryMomentMapper.getMonthlyHistoryMoment(parameterCode, getYearStartAndEndTimeMonthString(newDate).get("startTime"),getYearStartAndEndTimeMonthString(newDate).get("endTime"));
            }catch (Exception ignored){
            }
        }
        if(null != hourlyHistoryMoment){
            int year = getYear(newDate);
            List<Integer> collect = hourlyHistoryMoment.stream().map(TimeValueDTO::getTime).map(Integer::valueOf).collect(Collectors.toList());
            hourlyHistoryMoment.forEach(timeValueDTO->{
                timeValueDTO.setTime(year+"-"+String.format("%02d", Integer.valueOf(timeValueDTO.getTime())));
            });
            for (int i = 1; i <= 12; i++) {
                if(!collect.contains(i)){
                    TimeValueDTO timeValueDTO = new TimeValueDTO();
                    timeValueDTO.setTime(year+"-"+String.format("%02d", i));
                    timeValueDTO.setValue(0);
                    hourlyHistoryMoment.add(timeValueDTO);
                }
            }
            hourlyHistoryMoment.sort(Comparator.comparing(TimeValueDTO::getTime));
        }else {
            hourlyHistoryMoment = new ArrayList<>();
            int year = getYear(newDate);
            for (int i = 1; i <= 12; i++) {
                    TimeValueDTO timeValueDTO = new TimeValueDTO();
                    timeValueDTO.setTime(year+"-"+String.format("%02d", i));
                    timeValueDTO.setValue(0);
                    hourlyHistoryMoment.add(timeValueDTO);
            }
            hourlyHistoryMoment.sort(Comparator.comparing(TimeValueDTO::getTime));
        }

        result.setKeys(hourlyHistoryMoment.stream().map(TimeValueDTO::getTime).collect(Collectors.toList()));
        result.setValues(hourlyHistoryMoment.stream().map(TimeValueDTO::getValue).collect(Collectors.toList()));
        return result;
    }


    /**
     * @Author: liwencai
     * @Description: 获取群组设备信息
     * @Date: 2022/11/26
     * @Param sysCode: 子系统编码
     * @Param groupName: 组名
     * @Param areaName: 区域名
     * @Param keyword: 关键词
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: com.thtf.environment.dto.PageInfoVO
     */
    @Override
    public PageInfoVO listGroupedItemAlarmInfo(String sysCode,String groupName,String areaName,String keyword,Integer pageNumber,Integer pageSize) {
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        String areaCode = null;
        if(StringUtils.isNotBlank(areaName)){
            areaCode = adminAPI.searchAreaCodeByAreaName(areaName).getData();
        }
        ItemGroupKeywordParamDTO paramDTO = new ItemGroupKeywordParamDTO();
        paramDTO.setSystemCode(sysCode);
        paramDTO.setAreaCode(areaCode);
        paramDTO.setKeyword(keyword);
        paramDTO.setPageNumber(pageNumber);
        paramDTO.setPageSize(pageSize);
        paramDTO.setName(groupName);
        PageInfo<TblGroup> pageInfo = itemAPI.listGroupByKeywordOfNameAndAreaCodePage(paramDTO).getData();
        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(pageInfo);
        /* 获取全部的设备编码 */
        List<String> itemCodeList = new ArrayList<>();
        for (TblGroup group : pageInfo.getList()) {
            itemCodeList.addAll(Arrays.asList(group.getContainItemCodes().split(",")));
        }
        /* 获取全部区域编码 */
        List<String> areaCodeList = new ArrayList<>();
        for (TblGroup group : pageInfo.getList()) {
            areaCodeList.addAll(Arrays.asList(group.getBuildingAreaCodes().split(",")));
        }

        String areaCodes = areaCodeList.stream().distinct().collect(Collectors.joining(","));

        /* 获取全部建区域-筑编码映射 */
        Map<String, AreaNestBuildingDTO> areaNestBuildingMap = adminAPI.getAreaBuildingMap(areaCodes).getData();
//        /* 获取全部区域信息 */
//        List<CodeAndNameDTO> areaCodeAndNameList = adminAPI.listAreaNameListByAreaCodeList(areaCodeList).getData();
        /* 获取所有的参数 */
        List<TblItemParameter> parameterList = itemAPI.getParameterListByItemCodeListAndParameterTypeCodeList(itemCodeList,this.getParameterCodeList(parameterInfo)).getData();
        /* 所有类别相关的组信息 */
        Map<Long, Map<String, Object>> groupAboutItemType = itemTypeInfoOfGroup(parameterList, pageInfo.getList(), this.getParameterCodeList(parameterInfo));
        /* 匹配信息 */
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (TblGroup group : pageInfo.getList()) {
            Map<String, Object> map = groupAboutItemType.get(group.getId());
            map.put("id",group.getId());
            map.put("name",group.getName());
            // map.put("buildingCode",group)
            /* 匹配区域名称信息 */
            String[] split = group.getBuildingAreaCodes().split(",");
            StringBuilder areaStringBuilder = new StringBuilder();
            StringBuilder buildingStringBuilder = new StringBuilder();
            if(split.length>0){
                for (String area : split) {
                    AreaNestBuildingDTO areaNestBuildingDTO = areaNestBuildingMap.get(area);
                    if(null != areaNestBuildingDTO){
                        if(null != areaNestBuildingDTO.getAreaName() && StringUtils.isNotBlank(areaNestBuildingDTO.getAreaName())){
                            areaStringBuilder.append(areaNestBuildingDTO.getAreaName());
                            areaStringBuilder.append(",");
                        }
                        if(null != areaNestBuildingDTO.getBuildingName() && StringUtils.isNotBlank(areaNestBuildingDTO.getBuildingName())){
                            buildingStringBuilder.append(areaNestBuildingDTO.getBuildingName());
                            buildingStringBuilder.append(",");
                        }

                    }
                }
            }
            if(areaStringBuilder.length()>0){
                map.put("areaName",areaStringBuilder.toString().substring(0,areaStringBuilder.length()-1));
            }else {
                map.put("areaName",null);
            }

            if(buildingStringBuilder.length()>0){
                map.put("buildingName",buildingStringBuilder.toString().substring(0,buildingStringBuilder.length()-1));
            }else {
                map.put("buildingName",null);
            }

            /* 匹配不同类别的数据 */
            resultList.add(map);
        }
        List<Map<String, String>> title = new ArrayList<>();
        // List<String> allParameterCodeNeed = getAllParameterCodeNeed();
        List<String> allParameterCodeNeed = this.getParameterCodeList(parameterInfo);
        for (String parameterCode : allParameterCodeNeed) {
            Map<String, String> map1 = new HashMap<>();
            Map<String, String> map2 = new HashMap<>();
            map1.put("value",parameterCode);
            map1.put("name",this.getParameterNameByParameterTypeCode(parameterCode,parameterInfo));
            title.add(map1);
            map2.put("value",parameterCode+"_"+"itemTotalNum");
            map2.put("name","数量");
            title.add(map2);
        }
        pageInfoVO.setList(resultList);
        pageInfoVO.setOtherList(title);
        return pageInfoVO;
    }

    /**
     * @Author: liwencai
     * @Description: 区域环境情况
     * @Date: 2022/11/1
     * @Param: sysCode: 子系统编码
     * @Param: areaCode: 区域编码
     * @Param: buildingCodes: 建筑编码集
     * @Return: java.util.List<com.thtf.environment.vo.GroupAlarmInfoVO>
     */
    @Override
    public List<GroupAlarmInfoVO> getGroupAlarmDisplayInfo(String sysCode, String areaCode, String buildingCodes) {
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        List<GroupAlarmInfoVO> resultList = new ArrayList<>();
        /* 获取全部的分组id 根据sysCode */
        ItemGroupParamVO tblGroup = new ItemGroupParamVO();
        tblGroup.setSystemCode(sysCode);
        List<TblGroup> allGroup = itemAPI.queryAllGroup(tblGroup).getData();
        // 获取所有的分组id
        List<Long> groupIdList = allGroup.stream().map(TblGroup::getId).collect(Collectors.toList());
        List<ItemAlarmInfoDTO> groupAlarmInfo = itemAPI.countAlarmItemNumber(sysCode,groupIdList.stream().map(String::valueOf).collect(Collectors.toList())).getData();
        List<Long> alarmGroupIdList = new ArrayList<>();
        groupAlarmInfo.forEach(e->{
            if(e.getMalfunctionAlarmNumber()>0 || e.getMonitorAlarmNumber()>0 ){
                alarmGroupIdList.add(Long.valueOf(e.getAttribute().toString()));
            }
        });
        // List<TblItemType> itemTypeList = itemAPI.getItemTypesBySysId(sysCode).getBody().getData();
        List<String> itemTypeCodeList = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).collect(Collectors.toList());
//        List<String> itemTypeCodeList = itemTypeList.stream().map(TblItemType::getCode).collect(Collectors.toList());
        // 获取每个设备对应分组
        List<ItemTypeGroupListDTO> itemTypeGroupListDTO = itemAPI.listItemTypeNestedGroupKeyInfo(sysCode, itemTypeCodeList).getData();
        Map<String, List<TblGroup>> itemTypeGroupMap = new HashMap<>();
        itemTypeGroupListDTO.forEach(e->{
            itemTypeGroupMap.put(e.getItemTypeCode(),e.getGroupInfo());
        });
        for (ParameterTemplateAndDetailDTO parameterTemplateAndDetailDTO : parameterInfo) {
            GroupAlarmInfoVO groupAlarmInfoVO = new GroupAlarmInfoVO();
            String property;
            try {
                property = this.getParameterName(parameterTemplateAndDetailDTO.getItemTypeCode(),parameterInfo).split("[(]")[0].split("（")[0];
               // property = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(itemType.getCode()).getParameterTypeName();
            }catch (Exception e){
                property = parameterTemplateAndDetailDTO.getItemTypeName();
            }
            groupAlarmInfoVO.setProperty(property);
            groupAlarmInfoVO.setCode(parameterTemplateAndDetailDTO.getItemTypeCode());
            List<TblGroup> groupList = itemTypeGroupMap.get(parameterTemplateAndDetailDTO.getItemTypeCode());
            List<Long> list = new ArrayList<>(alarmGroupIdList);
            list.retainAll(groupList.stream().map(TblGroup::getId).collect(Collectors.toList()));
            groupAlarmInfoVO.setValue(list.size());
            resultList.add(groupAlarmInfoVO);
        }
        return resultList;
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/26
     * @Param sysCode:
     * @Param itemTypeCode:
     * @return: java.util.List<com.thtf.environment.vo.ItemCodeAndNameAndTypeVO>
     */
    @Override
    public List<ItemCodeAndNameAndTypeVO> listItemCodeAndTypeCodeByTypeCode(String sysCode, String itemTypeCode) {
        TblItem tblItem = new TblItem();
        tblItem.setSystemCode(sysCode);
        tblItem.setTypeCode(itemTypeCode);
        List<TblItem> data = itemAPI.queryAllItems(tblItem).getData();
        if(null == data || data.size() == 0){
            return null;
        }
        List<ItemCodeAndNameAndTypeVO> result = new ArrayList<>();
        data.forEach(e->{
            ItemCodeAndNameAndTypeVO itemCodeAndNameAndTypeVO = new ItemCodeAndNameAndTypeVO();
            itemCodeAndNameAndTypeVO.setItemCode(e.getCode());
            itemCodeAndNameAndTypeVO.setItemName(e.getName());
            itemCodeAndNameAndTypeVO.setItemTypeCode(e.getTypeCode());
            itemCodeAndNameAndTypeVO.setItemTypeName(e.getItemTypeName());
            result.add(itemCodeAndNameAndTypeVO);
        });
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 监测点位信息
     * @Date: 2022/12/5
     * @Param itemCode: 设备编码
     * @Return: java.lang.Object
     */
    @Override
    public EnvItemMonitorDTO getMonitorPointInfo(String itemCode) {
        TblItem data1 = itemAPI.searchItemByItemCode(itemCode).getData();
        if(null == data1){
            return null;
        }
        AtomicReference<String> monitorParameter = new AtomicReference<>();
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        parameterInfo.forEach(e->{
            if(e.getItemTypeCode().equals(data1.getTypeCode())){
                monitorParameter.set(e.getParameterType());
            }
        });
//         ItemMonitorPointInfoDTO monitorPointInfo = itemAPI.getMonitorPointInfo(itemCode).getData();
//        TblAlarmRecordUnhandle data = alarmAPI.getAlarmInfoByItemCodeLimitOne(itemCode).getData();
//        monitorPointInfo.setA
        EnvItemMonitorDTO result = new EnvItemMonitorDTO();
        ListItemNestedParametersParamDTO listItemNestedParametersParamDTO = new ListItemNestedParametersParamDTO();
        listItemNestedParametersParamDTO.setItemCodeList(Collections.singletonList(itemCode));

        List<String> parameterCodeList = new ArrayList<>();
        if(StringUtils.isNotBlank(monitorParameter.get())){
            parameterCodeList.add(monitorParameter.get());
        }
        // parameterCodeList.add(ParameterConstant.ENV_MONITOR_ONLINE);
        parameterCodeList.add(ParameterConstant.ENV_MONITOR_ALARM);
        listItemNestedParametersParamDTO.setParameterTypeCodeList(parameterCodeList);
        List<ListItemNestedParametersResultDTO> data = itemAPI.listItemNestedParameters(listItemNestedParametersParamDTO).getData();

        if(null != data && data.size() == 1){
            ListItemNestedParametersResultDTO listItemNestedParametersResultDTO = data.get(0);
            BeanUtils.copyProperties(listItemNestedParametersResultDTO,result);
            // 填写eye和center
            if(null != listItemNestedParametersResultDTO.getEye()){
                result.setEye(listItemNestedParametersResultDTO.getEye());
            }
            if(null != listItemNestedParametersResultDTO.getCenter()){
                result.setCenter(listItemNestedParametersResultDTO.getCenter());
            }
            List<TblItemParameter> resultParameterList = new ArrayList<>();
            List<TblItemParameter> parameterList = listItemNestedParametersResultDTO.getParameterList();
            parameterList.forEach(e->{
                if(ParameterConstant.ENV_MONITOR_ALARM.equals(e.getParameterType())){
                    result.setAlarmParameterCode(e.getCode());
                    result.setAlarmParameterValue(e.getValue());
                    resultParameterList.add(e);
                }
                if(StringUtils.isNotBlank(monitorParameter.get())){
                    if(monitorParameter.get().equals(e.getParameterType())){
                        result.setParameterCode(e.getCode());
                        result.setParameterValue(e.getValue());
                        resultParameterList.add(e);
                    }
                }

            });
            result.setParameterList(resultParameterList);
        }
        return result;
    }

    @Override
    public Object listParameterMap(ListParameterMapDTO listParameterMapDTO) {
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();

        Map<String, Object> resultMap = new HashMap<>();


        // 报警堆
        List<String> buildingCodeList = null;
        List<String> areaCodeList = null;
        TblItem tblItem = new TblItem();
        tblItem.setSystemCode(listParameterMapDTO.getSysCode());
        if(StringUtils.isNotBlank(listParameterMapDTO.getBuildingCodes())){
            buildingCodeList = Arrays.asList(listParameterMapDTO.getBuildingCodes().split(","));
        }else {
            if(StringUtils.isNotBlank(listParameterMapDTO.getAreaCodes())){
                areaCodeList = Arrays.asList(listParameterMapDTO.getAreaCodes().split(","));
            }
        }

        if(StringUtils.isNotBlank(listParameterMapDTO.getItemTypeCodes())){
            tblItem.setItemTypeCodeList(Arrays.asList(listParameterMapDTO.getItemTypeCodes().split(",")));
        }else {
            List<String> itemTypeCodeList = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).collect(Collectors.toList());
            tblItem.setItemTypeCodeList(itemTypeCodeList);
        }

        tblItem.setBuildingCodeList(buildingCodeList);
        tblItem.setAreaCodeList(areaCodeList);

        // 报警
        tblItem.setAlarm(1);
        List<TblItem> alarmItemList = itemAPI.queryAllItems(tblItem).getData();
        Map<String, String> alarmMap = getItemCodeAndTypeMap(alarmItemList);
        // 故障
        tblItem.setAlarm(0);
        tblItem.setFault(1);
        List<TblItem> faultItemList = itemAPI.queryAllItems(tblItem).getData();
        Map<String, String> faultMap = getItemCodeAndTypeMap(faultItemList);
        // 正常
        tblItem.setAlarm(0);
        tblItem.setFault(0);
        List<TblItem> normalList = itemAPI.queryAllItems(tblItem).getData();
        Map<String, String> normalMap = getItemCodeAndTypeMap(normalList);

        resultMap.put("alarm",alarmMap);
        resultMap.put("fault",faultMap);
        resultMap.put("normal",normalMap);
        return resultMap;
    }



    /**
     * @Author: liwencai
     * @Description: 形成设备堆
     * @Date: 2023/1/9
     * @Param tblItemList:
     * @Return: java.util.Map<java.lang.String,java.lang.String>
     */
    Map<String, String> getItemCodeAndTypeMap(List<TblItem> tblItemList){
        Map<String, String> map = new HashMap<>();
        tblItemList.forEach(e->{
            map.put(e.getCode(),e.getTypeCode());
        });
        return map;
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备类别详情
     * @Date: 2022/11/26
     * @Param parameterList:
     * @Param groupList:
     * @Param parameterCodeList:
     * @return: java.util.Map<java.lang.Long,java.util.Map<java.lang.String,java.lang.Object>>
     */
    public Map<Long,Map<String,Object>> itemTypeInfoOfGroup(List<TblItemParameter> parameterList,List<TblGroup> groupList,List<String> parameterCodeList){
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        Map<Long,Map<String,Object>> result = new HashMap<>();
        for (TblGroup group : groupList) {
            // 该组的设备编码
            List<String> itemCodeList = Arrays.asList(group.getContainItemCodes().split(","));
            // 遍历parameterCode
            Map<String, Object> map = new HashMap<>();
            for (String parameterCode : parameterCodeList) {
                Map<String, String> innerMap = new HashMap<>();
                // 设备总数
                int num = 0;
                // 需要统计计算平均值的总值
                double totalValue = (double) 0;
                // 需要统计计算平均值的总设备数
                int totalNum = 0;
                // 值单位
                if(null != parameterList && parameterList.size()>0){
                    List<TblItemParameter> collect = parameterList.stream().filter(e -> parameterCode.equals(e.getParameterType())).collect(Collectors.toList());

                    for (TblItemParameter itemParameter : collect) {
                        if(itemCodeList.contains(itemParameter.getItemCode())){
                            // 执行
                            if(null != itemParameter.getValue()){
                                totalValue += Double.parseDouble(itemParameter.getValue());
                                num ++;
                                totalNum ++;
                            }
                        }
                    }
                }
                innerMap.put("averageValue",percent(totalValue,totalNum,1));
                Map<String, String> minAndMaxValue = getMinAndMaxValue(parameterCode, parameterInfo);
                if(null != minAndMaxValue){
                    innerMap.put("configureMaxValue",minAndMaxValue.get("max"));
                    innerMap.put("configureMinValue",minAndMaxValue.get("min"));
                }
                // todo liwencai 设定值未知
                innerMap.put("configureValue","40");
                map.put(parameterCode,innerMap);
                map.put(parameterCode+"_"+"itemTotalNum",String.valueOf(num));
            }
            result.put(group.getId(),map);
        }
        return result;
    }


    /**
     * @Author: liwencai 
     * @Description:
     * @Date: 2022/11/23
     * @Param v1: 
     * @Param v2: 
     * @Param place: 
     * @return: java.lang.String 
     */
    public static String percent(double v1, double v2,int place) {
        String per = "0";
        if (v2 > 0) {
            NumberFormat percent = NumberFormat.getInstance();
            // 小数点后几位
            percent.setMaximumFractionDigits(place);
            per = percent.format(v1 / v2);

        } else {
            return per;
        }
        return per;
    }

    /* =============================== 复用代码区 ==================================== */

    private Map<String,String> getTodayStartTimeAndEndTimeString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
        Map<String,String> result = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        result.put("startTime",LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),0,0,0).format(dtf));
        result.put("endTime",LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),23,59,59).format(dtf));
        return result;
    }

    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH)+1;
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public Date YYMMDDStringToDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    Map<String, String> getMonthStartAndEndTimeDayString(Date date){
        Map<String, String> map = new HashMap<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int endDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 获取每月起始日
        int startDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH); // 获取每月最终日
        int year = calendar.get(Calendar.YEAR); // 获取年份
        int month = calendar.get(Calendar.MONTH);// 获取月份
        month++;
        map.put("startTime", dtf.format(LocalDateTime.of(year,month,startDay,0,0,0)));
        map.put("endTime",dtf.format(LocalDateTime.of(year,month,endDay,0,0,0)));
        return map;
    }


    Map<String, String> getYearStartAndEndTimeMonthString(Date date){
        Map<String, String> map = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int currentYear = calendar.get(Calendar.YEAR);
        map.put("startTime",dateFormat.format(getFirstOfYear(currentYear)));
        map.put("endTime",dateFormat.format(getLastOfYear(currentYear)));
        return map;
    }


    public static Date getFirstOfYear(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }


    public static Date getLastOfYear(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    /**
     * 设备点位参数值是否超过范围（设定值）
     */
    public boolean parameterValueIsTransfinite(String pValue,String pMax,String pMin){
        if(StringUtils.isBlank(pValue) && !StringUtils.isNumeric(pValue)){
            return false;
        }
        double value = ArithUtil.decimalPoint2(Double.parseDouble(pValue));
        // 比最大设定值大
        if(StringUtils.isNotBlank(pMax) && StringUtils.isNumeric(pMax)){
            double max = ArithUtil.decimalPoint2(Double.parseDouble(pMax));
            if(value>max){
                return true;
            }
        }
        // 比最小设定值小
        if(StringUtils.isNotBlank(pMin) && StringUtils.isNumeric(pMin)){
            double min = ArithUtil.decimalPoint2(Double.parseDouble(pMin));
            if(value<min){
                return true;
            }
        }
        return false;
    }


    public List<ParameterTemplateAndDetailDTO> getParameterInfo(){
        List<ItemTypeAndParameterTypeCodeDTO> itemTypeAndParameterTypeCodeList = parameterConfigNacos.getItemTypeAndParameterTypeCodeList();
        return itemAPI.listParameterDetail(itemTypeAndParameterTypeCodeList).getData();
    }

    public String getParameterType(String itemTypeCode,List<ParameterTemplateAndDetailDTO> parameterInfo){
        List<String> collect = parameterInfo.stream().filter(e -> e.getItemTypeCode().equals(itemTypeCode)).map(ParameterTemplateAndDetailDTO::getParameterType).collect(Collectors.toList());
        if(collect.size() == 0){
            return null;
        }else {
            return collect.get(0);
        }
    }

    public String getParameterName(String itemTypeCode,List<ParameterTemplateAndDetailDTO> parameterInfo){
        List<String> collect = parameterInfo.stream().filter(e -> e.getItemTypeCode().equals(itemTypeCode)).map(ParameterTemplateAndDetailDTO::getName).collect(Collectors.toList());
        if(collect.size() == 0){
            return null;
        }else {
            return collect.get(0);
        }
    }

    public List<String> getParameterCodeList(List<ParameterTemplateAndDetailDTO> parameterInfo){
        return parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getParameterType).collect(Collectors.toList());
    }

    public Map<String, String> getMinAndMaxValue(String parameterTypeCode,List<ParameterTemplateAndDetailDTO> parameterInfo){
        Map<String, String> resultMap = new HashMap<>();
        List<ParameterTemplateAndDetailDTO> collect = parameterInfo.stream().filter(e -> e.getParameterType().equals(parameterTypeCode)).collect(Collectors.toList());
        if(collect.size() == 0){
            return null;
        }else {
            resultMap.put("min",collect.get(0).getMin());
            resultMap.put("max",collect.get(0).getMax());
            return resultMap;
        }

    }

    public String getParameterNameByParameterTypeCode(String parameterTypeCode,List<ParameterTemplateAndDetailDTO> parameterInfo){
        List<String> collect = parameterInfo.stream().filter(e -> e.getParameterType().equals(parameterTypeCode)).map(ParameterTemplateAndDetailDTO::getName).collect(Collectors.toList());
        if(collect.size() == 0){
            return null;
        }else {
            return collect.get(0);
        }
    }

}
