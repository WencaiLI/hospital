package com.thtf.environment.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.thtf.common.constant.AlarmConstants;
import com.thtf.common.constant.ItemConstants;
import com.thtf.common.dto.adminserver.AreaNestBuildingDTO;
import com.thtf.common.dto.alarmserver.EChartsHourlyVO;
import com.thtf.common.dto.alarmserver.ItemAlarmInfoDTO;
import com.thtf.common.dto.alarmserver.ItemAlarmInfoVO;
import com.thtf.common.dto.alarmserver.TwentyFourHourAlarmStatisticsDTO;
import com.thtf.common.dto.itemserver.*;
import com.thtf.common.entity.itemserver.TblGroup;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.GroupAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.environment.config.ItemParameterConfig;
import com.thtf.environment.config.ParameterConfigNacos;
import com.thtf.environment.dto.PageInfoVO;
import com.thtf.environment.dto.*;
import com.thtf.environment.dto.convert.ItemTypeConvert;
import com.thtf.environment.dto.convert.PageInfoConvert;
import com.thtf.environment.dto.convert.ParameterConverter;
import com.thtf.environment.entity.TblHistoryMoment;
import com.thtf.environment.mapper.TblHistoryMomentMapper;
import com.thtf.environment.service.EnvMonitorService;
import com.thtf.environment.vo.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.hint.HintManager;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Resource
    private ItemAPI itemAPI;

    @Autowired
    private GroupAPI groupAPI;

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

    @Autowired
    private TblHistoryMomentMapper tblHistoryMomentMapper;

    @Resource
    private CommonService commonService;

    @Resource
    private ItemParameterConfig itemParameterConfig;

    @Resource
    private ParameterConfigNacos parameterConfigNacos;

    private final static String TBL_HISTORY_MOMENT = "tbl_history_moment";
    private final static String DAY_START_SUFFIX = " 00:00:00";
    private final static String DAY_END_SUFFIX = " 23:59:59";
    private final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private final static String YYYY_MM_DD = "yyyy-MM-dd";

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
        String parameterValueByStateExplain = commonService.getParameterValueByStateExplain(sysCode, itemParameterConfig.getState(), itemTypeCodes, new String[]{"运", "行", "在"});
        return itemAPI.getItemOnlineAndTotalAndAlarmItemNumber(sysCode,areaCode,buildingCodes,itemTypeCodes,itemParameterConfig.getState(),parameterValueByStateExplain,true,true).getData();
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
        if(CollectionUtils.isEmpty(parameterInfo)){
            return null;
        }
        EChartsMoreVO result = new EChartsMoreVO();
        List<KeyValueDTO> values = new ArrayList<>();
        List<String> itemTypeCodeList = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).distinct().collect(Collectors.toList());
        // 计算未处理的24小时统计
        TwentyFourHourAlarmStatisticsDTO param = new TwentyFourHourAlarmStatisticsDTO();
        param.setSysCode(sysCode);
        if(StringUtils.isNotBlank(areaCode)){
            param.setAreaCode(areaCode);
        }else {
            if(StringUtils.isNotBlank(buildingCodes)){
                param.setBuildingCodeList(Arrays.asList(buildingCodes.split(",")));
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
            }catch (Exception ignored){
            }
            keyValueDTO.setKey(property);
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
     * @Description: 获取所有报警的数据统计
     * @Date: 2023/2/28
     * @Param param:
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.common.dto.alarmserver.ItemAlarmInfoDTO>>
     */
    @Override
    public JsonResult<List<ItemAlarmInfoDTO>> getItemsAlarmInfo(ItemAlarmInfoVO param) {
        if(CollectionUtils.isEmpty(param.getItemTypeCodeList())){
            List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
            if(CollectionUtils.isEmpty(parameterInfo)){
                return null;
            }
            List<String> itemTypeCodeList = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).collect(Collectors.toList());
            param.setItemTypeCodeList(itemTypeCodeList);
        }
        return itemAPI.listTypeCodeItemAlarmInfo(param);
    }

    /**
     * 获取监测参数单位
     * @author liwencai
     * @param sysCode 子系统编码
     * @param itemTypeCodeList 设备类别编码集
     * @return {@link JsonResult<Map<String,String>>}
     */
    @Override
    public List<CodeUnitVO> getParameterUnit(String sysCode, List<String> itemTypeCodeList) {
        List<CodeUnitVO> result = new ArrayList<>();
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        parameterInfo.forEach(e->{
            result.add(CodeUnitVO.builder().code(e.getItemTypeCode()).unit(e.getUnit()).build());
        });
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
        if(CollectionUtils.isEmpty(parameterInfo)){
            return null;
        }
        // 在nacos 中配置的设备类别编码
        List<String> itemTypeList = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).collect(Collectors.toList());
        EChartsVO result = new EChartsVO();
        // 没传时间默认当天
        if(StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)){
            Date date = new Date();
            DateTime beginDateTime = DateUtil.beginOfDay(date);
            startTime = DateUtil.format(beginDateTime, YYYY_MM_DD_HH_MM_SS);
            DateTime lastDateTime = DateUtil.endOfDay(date);
            endTime = DateUtil.format(lastDateTime, YYYY_MM_DD_HH_MM_SS);
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
        initList = initList.stream().filter(e -> itemTypeList.contains(e.getAttribute().toString())).collect(Collectors.toList());
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
        if(CollectionUtils.isEmpty(parameterInfo)){
            return null;
        }
        List<String> itemTypeCodeList = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).collect(Collectors.toList());

        List<CodeNameVO> codeNameVOS = itemTypeConvert.toCodeNameVO(Objects.requireNonNull(itemAPI.getItemTypesBySysId(sysCode).getBody()).getData());
        codeNameVOS.removeIf(e->!itemTypeCodeList.contains(e.getCode()));
        return codeNameVOS;
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备信息
     * @Date: 2022/11/24
     * @Param paramVO:
     * @return: com.thtf.environment.dto.PageInfoVO
     */
    @Override
    public PageInfo<EnvMonitorItemResultVO> listItemInfo(EnvMonitorItemParamVO paramVO) {
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        if(CollectionUtils.isEmpty(parameterInfo)){
            return null;
        }
        // 所有设备类别编码
        List<String> buildingCodeList = StringUtils.isNotBlank(paramVO.getBuildingCodes())?Arrays.asList(paramVO.getBuildingCodes().split(",")):adminAPI.listBuildingCodeUserSelf().getData();;
        List<String> areaCodeList = StringUtils.isNotBlank(paramVO.getAreaCode())?Arrays.asList(paramVO.getAreaCode().split(",")):null;
        List<String> itemTypeCodeList = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).collect(Collectors.toList());
        if(StringUtils.isNotBlank(paramVO.getItemTypeCode())){
            itemTypeCodeList = Arrays.asList(paramVO.getItemTypeCode().split(","));
        }
        ListItemNestedParametersPageParamDTO paramDTO = new ListItemNestedParametersPageParamDTO();
        if(StringUtils.isNotBlank(paramVO.getKeyword())){
            paramDTO.setKeyword(paramVO.getKeyword());
            paramDTO.setCodeKey(paramVO.getKeyword());
            paramDTO.setNameKey(paramVO.getKeyword());
            paramDTO.setAreaKey(paramVO.getKeyword());
        }
        paramDTO.setSysCode(paramVO.getSysCode());
        paramDTO.setBuildingCodeList(buildingCodeList);
        paramDTO.setAreaCodeList(areaCodeList);
        if(itemTypeCodeList.size()>0){
            paramDTO.setItemTypeCodeList(itemTypeCodeList);
        }
        if(null != paramVO.getAlarmCategory()){
            // 报警
            if(AlarmConstants.ALARM_CATEGORY_INTEGER.equals(paramVO.getAlarmCategory())){
                paramDTO.setAlarm(ItemConstants.ITEM_ALARM_TRUE);
            }
            // 故障
            if(AlarmConstants.FAULT_CATEGORY_INTEGER.equals(paramVO.getAlarmCategory())){
                paramDTO.setAlarm(ItemConstants.ITEM_ALARM_FALSE);
                paramDTO.setFault(ItemConstants.ITEM_FAULT_TRUE);
            }
        }
        paramDTO.setPageNumber(paramVO.getPageNumber());
        paramDTO.setPageSize(paramVO.getPageSize());
        PageInfo<ItemNestedParameterVO> pageInfo = itemAPI.listItemNestedParametersPage(paramDTO).getData();

        PageInfo<EnvMonitorItemResultVO> pageInfoVO = new PageInfo<>();
        List<EnvMonitorItemResultVO> resultVOList = new ArrayList<>();
        Map<String, String> buildingInfoMap;

        // 建筑编码和建筑名称的映射
        if(!CollectionUtils.isEmpty(pageInfo.getList())){
            List<String> buildingCodeListInResult = pageInfo.getList().stream().map(ItemNestedParameterVO::getBuildingCode).distinct().collect(Collectors.toList());
            buildingInfoMap = adminAPI.getBuildingMap(buildingCodeListInResult).getData();
        }else {
            buildingInfoMap = new HashMap<>();
        }

        for (ItemNestedParameterVO item : pageInfo.getList()) {
            EnvMonitorItemResultVO envMonitorItemResultVO = new EnvMonitorItemResultVO();
            envMonitorItemResultVO.setItemCode(item.getCode());
            envMonitorItemResultVO.setItemTypeCode(item.getTypeCode());
            envMonitorItemResultVO.setItemName(item.getName());
            envMonitorItemResultVO.setAreaCode(item.getAreaCode());
            envMonitorItemResultVO.setBuildingCode(item.getBuildingCode());
            envMonitorItemResultVO.setBuildingName(buildingInfoMap.get(item.getBuildingCode()));
            envMonitorItemResultVO.setAreaName(item.getAreaName());
            envMonitorItemResultVO.setGroupId(item.getGroupId());
            envMonitorItemResultVO.setGroupName(item.getGroupName());
            // 匹配模型视角信息
            if(StringUtils.isNotBlank(item.getViewLongitude())){
                envMonitorItemResultVO.setEye(Arrays.stream(item.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            if(StringUtils.isNotBlank(item.getViewLatitude())){
                envMonitorItemResultVO.setCenter(Arrays.stream(item.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            // 匹配参数信息
            this.convertToParameter(envMonitorItemResultVO,item.getParameterList(),parameterInfo,itemTypeCodeList);

            if(ItemConstants.ITEM_ALARM_TRUE.equals(item.getAlarm())){
                envMonitorItemResultVO.setAlarmCategory(AlarmConstants.ALARM_CATEGORY_INTEGER);
                envMonitorItemResultVO.setFaultParameterCode(ItemConstants.ITEM_FAULT_FALSE.toString());
                envMonitorItemResultVO.setAlarmParameterValue(ItemConstants.ITEM_ALARM_TRUE.toString());
            }else if (ItemConstants.ITEM_ALARM_FALSE.equals(item.getAlarm()) && ItemConstants.ITEM_FAULT_TRUE.equals(item.getFault())){
                envMonitorItemResultVO.setAlarmCategory(AlarmConstants.FAULT_CATEGORY_INTEGER);
                envMonitorItemResultVO.setFaultParameterCode(ItemConstants.ITEM_FAULT_TRUE.toString());
                envMonitorItemResultVO.setAlarmParameterValue(ItemConstants.ITEM_ALARM_FALSE.toString());
            }else {
                envMonitorItemResultVO.setAlarmCategory(null);
                envMonitorItemResultVO.setFaultParameterCode(ItemConstants.ITEM_FAULT_FALSE.toString());
                envMonitorItemResultVO.setAlarmParameterValue(ItemConstants.ITEM_ALARM_FALSE.toString());
            }
            resultVOList.add(envMonitorItemResultVO);
        }
        pageInfoVO.setList(resultVOList);
        return pageInfoVO;
    }

    /**
     * @Author: liwencai
     * @Description: 匹配参数
     * @Date: 2023/3/10
     * @Param envMonitorItemResultVO:
     * @Param itemParameterList:
     * @Param parameterInfo:
     * @Param itemTypeCodeList:
     * @Return: void
     */
    void convertToParameter(EnvMonitorItemResultVO envMonitorItemResultVO,List<TblItemParameter> itemParameterList,List<ParameterTemplateAndDetailDTO>  parameterInfo,List<String> itemTypeCodeList){
        // 匹配参数
        itemParameterList.forEach(parameter->{
            if(parameter.getItemCode().equals(envMonitorItemResultVO.getItemCode())){
                // 在线状态
                if(itemParameterConfig.getState().equals(parameter.getParameterType())){
                    envMonitorItemResultVO.setOnlineParameterCode(parameter.getCode());
                    if(null != parameter.getValue()){
                        envMonitorItemResultVO.setOnlineParameterValue(Optional.ofNullable(parameter.getValue()).orElse("")+Optional.ofNullable(parameter.getUnit()).orElse(""));
                    }
                }
                // 故障
                if(itemParameterConfig.getFault().equals(parameter.getParameterType())){
                    envMonitorItemResultVO.setFaultParameterCode(parameter.getCode());
                    if(null != parameter.getValue()){
                        envMonitorItemResultVO.setFaultParameterValue(Optional.ofNullable(parameter.getValue()).orElse("")+Optional.ofNullable(parameter.getUnit()).orElse(""));
                    }
                }
                // 报警
                if(itemParameterConfig.getAlarm().equals(parameter.getParameterType())){
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
     * @Description: 获取每小时的历史统计数据
     * @Date: 2022/10/27
     * @Param: itemCode: 设备编码
     * @Param: itemTypeCode: 设备类别编码
     * @Param: parameterCode: 参数编码
     * @Param: date: 日期
     * @Return: com.thtf.environment.vo.EChartsVO
     */
    @Override
    public EChartsVO getHourlyHistoryMoment(String itemCode,String itemTypeCode, String parameterCode, String date) {
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        if(CollectionUtils.isEmpty(parameterInfo)){
            return null;
        }

        EChartsVO result = new EChartsVO();
        parameterInfo.forEach(e->{
            if (e.getItemTypeCode().equals(itemTypeCode)){
                result.setUnit(e.getUnit());
            }
        });

        List<TimeValueDTO> hourlyHistoryMoment = null;
        if(StringUtils.isBlank(parameterCode)){
            if(StringUtils.isNotBlank(itemTypeCode)){
                String parameterType = this.getParameterType(itemTypeCode,parameterInfo);
                parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
            }else if(StringUtils.isNotBlank(itemCode)){
                TblItem tblItem = new TblItem();
                tblItem.setCode(itemCode);
                List<TblItem> data = itemAPI.queryAllItems(tblItem).getData();
                if(null != data && data.size()>0){
                    String parameterType = this.getParameterType(itemTypeCode,parameterInfo);
                    parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
                }
            }
        }
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.addTableShardingValue(TBL_HISTORY_MOMENT,date+DAY_START_SUFFIX);
            hourlyHistoryMoment = tblHistoryMomentMapper.getHourlyHistoryMoment(parameterCode,date+DAY_START_SUFFIX,date+DAY_END_SUFFIX);
        }catch (Exception ignored){
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
                    String parameterType = this.getParameterType(itemTypeCode,parameterInfo);
                    parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
                }else if(StringUtils.isNotBlank(itemCode)){
                    TblItem tblItem = new TblItem();
                    tblItem.setCode(itemCode);
                    List<TblItem> data = itemAPI.queryAllItems(tblItem).getData();
                    if(!CollectionUtils.isEmpty(data)){
                        String parameterType = this.getParameterType(itemTypeCode,parameterInfo);
                        parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
                    }
                }
            }
            try {
                // 获取月开始和结束时间
                DateTime beginDateTime = DateUtil.beginOfMonth(newDate);
                String startDateTime = DateUtil.format(beginDateTime, YYYY_MM_DD_HH_MM_SS);
                DateTime lastDateTime = DateUtil.endOfMonth(newDate);
                String endDateTime = DateUtil.format(lastDateTime, YYYY_MM_DD_HH_MM_SS);
                hourlyHistoryMoment = tblHistoryMomentMapper.getDailyHistoryMoment(parameterCode, startDateTime, endDateTime);
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
        int year = DateUtil.year(newDate);
        int month = DateUtil.month(newDate);
        month +=1;
        int lengthOfMonth = DateUtil.lengthOfMonth(month,DateUtil.isLeapYear(year));
        String timePrefix = year+"-"+String.format("%02d", month)+"-";
        // 为null补0
        hourlyHistoryMoment.forEach(e->{
            e.setTime(timePrefix+String.format("%02d", Integer.valueOf(e.getTime())));
        });
        for (int i = 1; i <= lengthOfMonth; i++) {
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
     * @Description: 逐月计算历史数据
     * @Date: 2022/10/27
     * @Param: itemCode: 设备编码
     * @Param: itemTypeCode: 设备类别编码
     * @Param: parameterCode: 参数编码
     * @Param: date: 日期
     * @Return: com.thtf.environment.vo.EChartsVO
     */
    @Override
    public EChartsVO getMonthlyHistoryMoment(String itemCode, String itemTypeCode, String parameterCode, String date) {
        EChartsVO result = new EChartsVO();
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        Date newDate = YYMMDDStringToDate(date);
        if(null == newDate){
            return null;
        }
        parameterInfo.forEach(e->{
            if (e.getItemTypeCode().equals(itemTypeCode)){
                result.setUnit(e.getUnit());
            }
        });
        List<TimeValueDTO> hourlyHistoryMoment = null;
        try (HintManager hintManager = HintManager.getInstance()) {
            // 日期是当年的开始时间，至本月时间
            DateTime beginDateTime = DateUtil.beginOfYear(newDate);
            String startDateTime = DateUtil.format(beginDateTime, YYYY_MM_DD_HH_MM_SS);
            DateTime lastDateTime = DateUtil.endOfYear(newDate);
            String endDateTime = DateUtil.format(lastDateTime, YYYY_MM_DD_HH_MM_SS);
            hintManager.addTableShardingValue(TBL_HISTORY_MOMENT, startDateTime);
            hintManager.addTableShardingValue(TBL_HISTORY_MOMENT, endDateTime);
            if(StringUtils.isBlank(parameterCode)){
                if(StringUtils.isNotBlank(itemTypeCode)){
                    String parameterType = this.getParameterType(itemTypeCode,parameterInfo);
                    parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
                }else if(StringUtils.isNotBlank(itemCode)){
                    TblItem tblItem = new TblItem();
                    tblItem.setCode(itemCode);
                    List<TblItem> data = itemAPI.queryAllItems(tblItem).getData();
                    if(null != data && data.size()>0){
                        String parameterType = this.getParameterType(itemTypeCode,parameterInfo);
                        parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
                    }
                }
            }
            try {
                hourlyHistoryMoment = tblHistoryMomentMapper.getMonthlyHistoryMoment(parameterCode, startDateTime,endDateTime);
            }catch (Exception ignored){
            }
        }
        if(null != hourlyHistoryMoment){
            int year = DateUtil.year(newDate);
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
            int year = DateUtil.year(newDate);
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
    public PageInfoVO listGroupedItemAlarmInfo(String sysCode,String buildingCodes, String areaCode,String groupName,String areaName,String keyword,Integer pageNumber,Integer pageSize) {
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();
        if(CollectionUtils.isEmpty(parameterInfo)){
            return null;
        }
        // 所有设备类别编码
        List<String> itemTypeCodeList = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).collect(Collectors.toList());
        // groupName areaName 没有参与筛选
        ItemGroupParamVO paramVO = new ItemGroupParamVO();
        paramVO.setSystemCode(sysCode);
        paramVO.setBuildingCodes(buildingCodes);
        paramVO.setBuildingAreaCodes(areaCode);
        paramVO.setKeyword(keyword);
        paramVO.setItemTypeCodeList(itemTypeCodeList);
        paramVO.setPageNumber(pageNumber);
        paramVO.setPageSize(pageSize);
        PageInfo<TblGroup> pageInfo = itemAPI.queryAllGroupPage(paramVO).getData();
        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(pageInfo);
        if(null == pageInfo || null == pageInfo.getList()){
            return pageInfoVO;
        }
        /* 获取全部的设备编码 */
        List<String> itemCodeList = new ArrayList<>();
        for (TblGroup group : pageInfo.getList()) {
            if(!CollectionUtils.isEmpty(group.getItemCodeList())){
                itemCodeList.addAll(group.getItemCodeList());
            }
        }
        /* 获取全部区域编码 */
        List<String> areaCodeList = new ArrayList<>();
        for (TblGroup group : pageInfo.getList()) {
            areaCodeList.addAll(Arrays.asList(group.getBuildingAreaCodes().split(",")));
        }
        String areaCodes = areaCodeList.stream().distinct().collect(Collectors.joining(","));
        /* 获取全部建区域-筑编码映射 */
        Map<String, AreaNestBuildingDTO> areaNestBuildingMap = adminAPI.getAreaBuildingMap(areaCodes).getData();
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
        List<String> itemTypeCodeList = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).collect(Collectors.toList());
        ListGroupInfoParamDTO paramDTO = new ListGroupInfoParamDTO();
        paramDTO.setSysCode(sysCode);
        paramDTO.setBuildingCodes(buildingCodes);
        paramDTO.setAreaCode(areaCode);
        paramDTO.setItemTypeCodeList(itemTypeCodeList);
        paramDTO.setParameterTemplateAndDetailList(parameterInfo);
        return groupAPI.listGroupInfoByItemCodeList(paramDTO).getData();
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
        if(CollectionUtils.isEmpty(data)){
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

        EnvItemMonitorDTO result = new EnvItemMonitorDTO();
        if(ItemConstants.ITEM_ALARM_TRUE.equals(data1.getAlarm())){
            result.setAlarmCategory(AlarmConstants.ALARM_CATEGORY_INTEGER);
        }else if(ItemConstants.ITEM_FAULT_TRUE.equals(data1.getFault()) && ItemConstants.ITEM_FAULT_FALSE.equals(data1.getAlarm())){
            result.setAlarmCategory(AlarmConstants.FAULT_CATEGORY_INTEGER);
        }
        ListItemNestedParametersParamDTO listItemNestedParametersParamDTO = new ListItemNestedParametersParamDTO();
        listItemNestedParametersParamDTO.setItemCodeList(Collections.singletonList(itemCode));

        List<ListItemNestedParametersResultDTO> data = itemAPI.listItemNestedParameters(listItemNestedParametersParamDTO).getData();

        if(!CollectionUtils.isEmpty(data)){
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
                if(itemParameterConfig.getAlarm().equals(e.getParameterType())){
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

    /**
     * @Author: liwencai
     * @Description: 获取参数映射关系
     * @Date: 2023/3/10
     * @Param listParameterMapDTO:
     * @Return: java.lang.Object
     */
    @Override
    public Object listParameterMap(ListParameterMapDTO listParameterMapDTO) {
        List<ParameterTemplateAndDetailDTO> parameterInfo = getParameterInfo();

        Map<String, Object> resultMap = new HashMap<>();

        // 报警堆
        List<String> buildingCodeList = StringUtils.isNotBlank(listParameterMapDTO.getBuildingCodes())?Arrays.asList(listParameterMapDTO.getBuildingCodes().split(",")):adminAPI.listBuildingCodeUserSelf().getData();
        List<String> areaCodeList = StringUtils.isNotBlank(listParameterMapDTO.getAreaCodes())?Arrays.asList(listParameterMapDTO.getAreaCodes().split(",")):null;
        TblItem tblItem = new TblItem();
        tblItem.setSystemCode(listParameterMapDTO.getSysCode());

        if(StringUtils.isNotBlank(listParameterMapDTO.getItemTypeCodes())){
            tblItem.setItemTypeCodeList(Arrays.asList(listParameterMapDTO.getItemTypeCodes().split(",")));
        }else {
            List<String> itemTypeCodeList = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).collect(Collectors.toList());
            tblItem.setItemTypeCodeList(itemTypeCodeList);
        }

        tblItem.setBuildingCodeList(buildingCodeList);
        tblItem.setAreaCodeList(areaCodeList);

        // 报警
        tblItem.setAlarm(ItemConstants.ITEM_ALARM_TRUE);
        List<TblItem> alarmItemList = itemAPI.queryAllItems(tblItem).getData();
        Map<String, String> alarmMap = getItemCodeAndTypeMap(alarmItemList);
        // 故障
        tblItem.setAlarm(ItemConstants.ITEM_ALARM_FALSE);
        tblItem.setFault(ItemConstants.ITEM_FAULT_TRUE);
        List<TblItem> faultItemList = itemAPI.queryAllItems(tblItem).getData();
        Map<String, String> faultMap = getItemCodeAndTypeMap(faultItemList);
        // 正常
        tblItem.setAlarm(ItemConstants.ITEM_ALARM_FALSE);
        tblItem.setFault(ItemConstants.ITEM_FAULT_FALSE);
        List<TblItem> normalList = itemAPI.queryAllItems(tblItem).getData();
        Map<String, String> normalMap = getItemCodeAndTypeMap(normalList);

        resultMap.put("alarm",alarmMap);
        resultMap.put("fault",faultMap);
        resultMap.put("normal",normalMap);
        return resultMap;
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
            List<String> itemCodeList = group.getItemCodeList();
            // 遍历parameterCode
            Map<String, Object> map = new HashMap<>();
            for (String parameterCode : parameterCodeList) {
                Map<String, String> innerMap = new HashMap<>();
                // 设备总数
                int num = 0;
                // 需要统计计算平均值的总值
                double totalValue = 0;
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

    /* =============================== 复用代码区 ==================================== */

    /**
     * @Author: liwencai
     * @Description: 除数并保留place位小数
     * @Date: 2022/11/23
     * @Param v1: 被除数
     * @Param v2: 除数
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

    /**
     * @Author: liwencai
     * @Description: 形成设备堆
     * @Date: 2023/1/9
     * @Param tblItemList: 设备信息集
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
     * @Description: 将String类型的日期转换为yyyy-MM-dd HH:mm:ss格式
     * @Date: 2023/3/10
     * @Param date: String类型的日期
     * @Return: java.util.Date
     */
    public Date YYMMDDStringToDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备参数详情
     * @Date: 2023/3/10
     * @Return: java.util.List<com.thtf.common.dto.itemserver.ParameterTemplateAndDetailDTO>
     */
    public List<ParameterTemplateAndDetailDTO> getParameterInfo(){
        List<ItemTypeAndParameterTypeCodeDTO> itemTypeAndParameterTypeCodeList = parameterConfigNacos.getItemTypeAndParameterTypeCodeList();
        return itemAPI.listParameterDetail(itemTypeAndParameterTypeCodeList).getData();
    }

    /**
     * @Author: liwencai
     * @Description: 获取参数类别
     * @Date: 2023/3/10
     * @Param itemTypeCode: 设备类别编码
     * @Param parameterInfo: 参数信息集
     * @Return: java.lang.String
     */
    public String getParameterType(String itemTypeCode,List<ParameterTemplateAndDetailDTO> parameterInfo){
        List<String> collect = parameterInfo.stream().filter(e -> e.getItemTypeCode().equals(itemTypeCode)).map(ParameterTemplateAndDetailDTO::getParameterType).collect(Collectors.toList());
        if(collect.size() == 0){
            return null;
        }else {
            return collect.get(0);
        }
    }

    /**
     * @Author: liwencai
     * @Description: 获取参数名
     * @Date: 2023/3/10
     * @Param itemTypeCode: 设备类别编码
     * @Param parameterInfo: 参数信息集
     * @Return: java.lang.String
     */
    public String getParameterName(String itemTypeCode,List<ParameterTemplateAndDetailDTO> parameterInfo){
        List<String> collect = parameterInfo.stream().filter(e -> e.getItemTypeCode().equals(itemTypeCode)).map(ParameterTemplateAndDetailDTO::getName).collect(Collectors.toList());
        if(collect.size() == 0){
            return null;
        }else {
            return collect.get(0);
        }
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备参数编码集
     * @Date: 2023/3/10
     * @Param parameterInfo: 参数信息集
     * @Return: java.util.List<java.lang.String>
     */
    public List<String> getParameterCodeList(List<ParameterTemplateAndDetailDTO> parameterInfo){
        return parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getParameterType).collect(Collectors.toList());
    }

    /**
     * @Author: liwencai
     * @Description: 获取某参数的设定最大和最小值
     * @Date: 2023/3/10
     * @Param parameterTypeCode: 参数类别编码
     * @Param parameterInfo: 参数信息集
     * @Return: java.util.Map<java.lang.String,java.lang.String>
     */
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

    /**
     * @Author: liwencai
     * @Description: 根据参数类别编码获取参数名称
     * @Date: 2023/3/10
     * @Param parameterTypeCode: 参数类别编码
     * @Param parameterInfo: 参数信息集
     * @Return: java.lang.String
     */
    public String getParameterNameByParameterTypeCode(String parameterTypeCode,List<ParameterTemplateAndDetailDTO> parameterInfo){
        List<String> collect = parameterInfo.stream().filter(e -> e.getParameterType().equals(parameterTypeCode)).map(ParameterTemplateAndDetailDTO::getName).collect(Collectors.toList());
        if(collect.size() == 0){
            return null;
        }else {
            return collect.get(0);
        }
    }

}
