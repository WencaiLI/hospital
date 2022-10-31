package com.thtf.environment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.alarmserver.ItemAlarmInfoDTO;
import com.thtf.common.dto.itemserver.CodeAndNameDTO;
import com.thtf.common.dto.itemserver.ItemGroupKeywordParamDTO;
import com.thtf.common.dto.itemserver.ItemTotalAndOnlineAndAlarmNumDTO;
import com.thtf.common.entity.adminserver.TblBuildingArea;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblGroup;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.entity.itemserver.TblItemType;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.environment.common.enums.EnvMonitorItemLiveParameterEnum;
import com.thtf.environment.dto.*;
import com.thtf.environment.dto.convert.ItemParameterConvert;
import com.thtf.environment.dto.convert.ItemTypeConvert;
import com.thtf.environment.dto.convert.PageInfoConvert;
import com.thtf.environment.entity.TblHistoryMoment;
import com.thtf.environment.mapper.TblHistoryMomentMapper;
import com.thtf.environment.service.EnvMonitorService;
import com.thtf.environment.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.hint.HintManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: liwencai
 * @Date: 2022/10/25 14:28
 * @Description:
 */
@Service
public class EnvMonitorServiceImpl extends ServiceImpl<TblHistoryMomentMapper, TblHistoryMoment> implements EnvMonitorService {

    @Autowired
    TblHistoryMomentMapper tblHistoryMomentMapper;

    @Resource
    ItemAPI itemAPI;

    @Resource
    AlarmAPI alarmAPI;

    @Resource
    AdminAPI adminAPI;

    @Resource
    ItemTypeConvert itemTypeConvert;

    @Resource
    PageInfoConvert pageInfoConvert;

    @Resource
    ItemParameterConvert itemParameterConvert;

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
        return itemAPI.getItemOnlineAndTotalAndAlarmItemNumber(sysCode,areaCode,buildingCodes).getData();
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
        result.setKeys(initList.stream().map(ItemAlarmInfoDTO::getAttribute).map(Object::toString).collect(Collectors.toList()));
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
        return itemTypeConvert.toCodeNameVO(itemAPI.getItemTypesBySysId(sysCode).getBody().getData());
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备信息
     * @Date: 2022/10/27
     * @Param: paramVO:
     * @Return: java.util.List<com.thtf.environment.vo.EnvMonitorItemResultVO>
     */
    @Override
    public PageInfoVO listItemInfo(EnvMonitorItemParamVO paramVO) {
        List<EnvMonitorItemResultVO> resultVOList = new ArrayList<>();
        List<String> itemCodeList;
        List<String> groupIdStringList;
        if(null == paramVO.getAlarmCategory()){
            PageInfo<TblItem> pageInfo = itemAPI.searchItemBySysCodeAndTypeCodeAndAreaCodeListAndKeywordPage(paramVO.getSysCode(), paramVO.getItemTypeCode(), paramVO.getKeyword(), null, paramVO.getPageNumber(), paramVO.getPageSize()).getData();
            // 设备类别编码集
            itemCodeList = pageInfo.getList().stream().map(TblItem::getCode).collect(Collectors.toList());
            // 查询设备参数集
            List<TblItemParameter> itemParameterList = itemAPI.searchParameterByItemCodes(itemCodeList).getData();
            // 分组id集
            groupIdStringList = pageInfo.getList().stream().filter(e->null != e.getGroupId()).map(TblItem::getGroupId).map(String::valueOf).collect(Collectors.toList());
            List<TblGroup> groupList = null;
            if(groupIdStringList.size()>0){
               groupList = itemAPI.searchGroupByIdList(groupIdStringList).getData();
            }
            // 分组信息集
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

                if(StringUtils.isNotBlank(item.getViewLongitude())){
                    envMonitorItemResultVO.setEye(Arrays.stream(item.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                }
                if(StringUtils.isNotBlank(item.getViewLatitude())){
                    envMonitorItemResultVO.setCenter(Arrays.stream(item.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                }
                // 匹配参数
                List<TblItemParameter> inner = new ArrayList<>();
                itemParameterList.forEach(parameter->{
                    if(parameter.getItemCode().equals(item.getCode())){
                        inner.add(parameter);
                    }
                });
                envMonitorItemResultVO.setParameterList(inner);
                resultVOList.add(envMonitorItemResultVO);
            }
            pageInfoVO.setList(resultVOList);
            return pageInfoVO;
        }else {
            PageInfo<TblAlarmRecordUnhandle> pageInfo = alarmAPI.getAlarmInfoBySysCodeAndCategoryLimitOneByKeywordPage(paramVO.getKeyword(), paramVO.getAlarmCategory(), paramVO.getSysCode(), paramVO.getPageNumber(), paramVO.getPageSize()).getData();
            itemCodeList = pageInfo.getList().stream().map(TblAlarmRecordUnhandle::getItemCode).collect(Collectors.toList());

            List<TblItemParameter> itemParameterList = itemAPI.searchParameterByItemCodes(itemCodeList).getData();
            itemCodeList = pageInfo.getList().stream().map(TblAlarmRecordUnhandle::getItemCode).collect(Collectors.toList());
            List<TblItem> itemList = itemAPI.searchItemByItemCodeList(itemCodeList).getData();
            PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(pageInfo);
            // 分组id集
            groupIdStringList = itemList.stream().filter(e->null != e.getGroupId()).map(TblItem::getGroupId).map(String::valueOf).collect(Collectors.toList());
            List<TblGroup> groupList = null;
            if(groupIdStringList.size()>0){
                groupList = itemAPI.searchGroupByIdList(groupIdStringList).getData();
            }
            for (TblAlarmRecordUnhandle alarmRecord: pageInfo.getList()) {
                EnvMonitorItemResultVO envMonitorItemResultVO = new EnvMonitorItemResultVO();
                // 匹配设备信息
                List<TblGroup> finalGroupList = groupList;
                itemList.forEach(item->{
                    if(item.getCode().equals(alarmRecord.getItemCode())){
                        envMonitorItemResultVO.setItemCode(item.getCode());
                        envMonitorItemResultVO.setItemName(item.getName());
                        envMonitorItemResultVO.setAreaCode(item.getAreaCode());
                        envMonitorItemResultVO.setAreaName(item.getAreaName());
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
                // 匹配参数
                List<TblItemParameter> inner = new ArrayList<>();

                itemParameterList.forEach(parameter->{
                    if(parameter.getItemCode().equals(alarmRecord.getItemCode())){
                        inner.add(parameter);
                    }
                });
                envMonitorItemResultVO.setParameterList(inner);
                resultVOList.add(envMonitorItemResultVO);
            }
            pageInfoVO.setList(resultVOList);
            return pageInfoVO;
        }
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
        return itemParameterConvert.toItemParameterInfoVO(itemAPI.searchParameterByItemCodes(Collections.singletonList(itemCode)).getData());
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
        List<TimeValueDTO> hourlyHistoryMoment;
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.addTableShardingValue(TBL_HISTORY_MOMENT,date+DAY_START_SUFFIX);
            if(StringUtils.isBlank(parameterCode)){
                String parameterType = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(itemTypeCode).getParameterType();
                parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
            }
            hourlyHistoryMoment = tblHistoryMomentMapper.getHourlyHistoryMoment(parameterCode,date+DAY_START_SUFFIX,date+DAY_END_SUFFIX);
        }
        List<Integer> collect = hourlyHistoryMoment.stream().map(TimeValueDTO::getTime).map(Integer::valueOf).collect(Collectors.toList());
        for (int i = 0; i < 24; i++) {
            if(!collect.contains(i)){
                TimeValueDTO timeValueDTO = new TimeValueDTO();
                timeValueDTO.setTime(String.format("%02d", i));
                timeValueDTO.setValue(0);
                hourlyHistoryMoment.add(timeValueDTO);
            }
        }
        hourlyHistoryMoment.sort(Comparator.comparing(TimeValueDTO::getValue));
        EChartsVO result = new EChartsVO();
        result.setKeys(hourlyHistoryMoment.stream().map(TimeValueDTO::getTime).collect(Collectors.toList()));
        result.setValues(hourlyHistoryMoment.stream().map(TimeValueDTO::getValue).collect(Collectors.toList()));
        return result;
    }

    @Override
    public EChartsVO getDailyHistoryMoment(String itemCode, String itemTypeCode, String parameterCode, String date) {
        Date newDate = YYMMDDStringToDate(date);
        if(null == date){
            log.error("时间格式错误");
            return null;
        }
        List<TimeValueDTO> hourlyHistoryMoment;
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.addTableShardingValue(TBL_HISTORY_MOMENT,date+DAY_END_SUFFIX);
            if(StringUtils.isBlank(parameterCode)){
               parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(itemTypeCode).getParameterType(),itemCode).getData();
            }
            hourlyHistoryMoment = tblHistoryMomentMapper.getDailyHistoryMoment(parameterCode, getMonthStartAndEndTimeDayString(newDate).get("startTime"),getMonthStartAndEndTimeDayString(newDate).get("endTime"));
        }
        List<Integer> collect = hourlyHistoryMoment.stream().map(TimeValueDTO::getTime).map(Integer::valueOf).collect(Collectors.toList());
        // 填充数据
        int year = getYear(newDate);
        int month = getMonth(newDate);
        int daysOfMonth = getDaysOfMonth(newDate);
        String timePrefix = year+"-"+month+"-";
        for (int i = 1; i <= daysOfMonth; i++) {
            if(!collect.contains(i)){
                TimeValueDTO timeValueDTO = new TimeValueDTO();
                timeValueDTO.setTime(timePrefix+String.format("%02d", i));
                timeValueDTO.setValue(0);
                hourlyHistoryMoment.add(timeValueDTO);
            }
        }
        // 排序
        hourlyHistoryMoment.sort(Comparator.comparing(TimeValueDTO::getValue));
        EChartsVO result = new EChartsVO();
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
        Date newDate = YYMMDDStringToDate(date);
        if(null == date){
            log.error("时间格式错误");
            return null;
        }
        List<TimeValueDTO> hourlyHistoryMoment;
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.addTableShardingValue(TBL_HISTORY_MOMENT,date+DAY_START_SUFFIX);
            if(StringUtils.isBlank(parameterCode)){
                parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(itemTypeCode).getParameterType(),itemCode).getData();
            }
            hourlyHistoryMoment = tblHistoryMomentMapper.getMonthlyHistoryMoment(parameterCode, getYearStartAndEndTimeMonthString(newDate).get("startTime"),getYearStartAndEndTimeMonthString(newDate).get("endTime"));
        }
        List<Integer> collect = hourlyHistoryMoment.stream().map(TimeValueDTO::getTime).map(Integer::valueOf).collect(Collectors.toList());


        int year = getYear(newDate);
        for (int i = 1; i <= 12; i++) {
            if(!collect.contains(i)){
                TimeValueDTO timeValueDTO = new TimeValueDTO();
                timeValueDTO.setTime(year+"-"+String.format("%02d", i));
                timeValueDTO.setValue(0);
                hourlyHistoryMoment.add(timeValueDTO);
            }
        }
        hourlyHistoryMoment.sort(Comparator.comparing(TimeValueDTO::getValue));
        EChartsVO result = new EChartsVO();
        result.setKeys(hourlyHistoryMoment.stream().map(TimeValueDTO::getTime).collect(Collectors.toList()));
        result.setValues(hourlyHistoryMoment.stream().map(TimeValueDTO::getValue).collect(Collectors.toList()));
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 获取分组信息
     * @Date: 2022/10/30
     * @Param sysCode: 子系统编码
     * @return: com.thtf.environment.dto.PageInfoVO
     */
    @Override
    public PageInfoVO listGroupedItemAlarmInfo(String sysCode,String groupName,String areaName,String keyword,Integer pageNumber,Integer pageSize) {
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

        // List<TblItemType> itemTypeList = itemAPI.getItemTypesBySysId(sysCode).getBody().getData();
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
        /* 获取全部区域信息 */
        List<CodeAndNameDTO> areaCodeAndNameList = adminAPI.listAreaNameListByAreaCodeList(areaCodeList).getData();
        /* 获取所有的参数 */
        List<TblItemParameter> parameterList = itemAPI.getParameterListByItemCodeListAndParameterTypeCodeList(itemCodeList,this.getAllParameterCodeNeed()).getData();
        /* 所有类别相关的组信息 */
        Map<Long, List<EnvMonitorItemTypeDTO>> groupAboutItemType = itemTypeInfoOfGroup(parameterList, pageInfo.getList(), this.getAllParameterCodeNeed());

        /* 匹配信息 */
        List<EnvMonitorGroupDTO> resultList = new ArrayList<>();
        for (TblGroup group : pageInfo.getList()) {
            EnvMonitorGroupDTO envMonitorGroupDTO = new EnvMonitorGroupDTO();
            envMonitorGroupDTO.setId(group.getId());
            envMonitorGroupDTO.setName(group.getName());
            /* 匹配区域名称信息 */
            String[] split = group.getBuildingAreaCodes().split(",");
            StringBuilder stringBuilder = new StringBuilder();
            if(split.length>0){
                for (String area : split) {
                    areaCodeAndNameList.forEach(e->{
                        if(e.getCode().equals(area)){
                            stringBuilder.append(e.getName());
                            stringBuilder.append(",");
                        }
                    });
                }
            }
            if(stringBuilder.length()>0){
                envMonitorGroupDTO.setAreaName(stringBuilder.toString().substring(0,stringBuilder.length()-1)); // 去除‘，’
            }
            /* 匹配不同类别的数据 */
            envMonitorGroupDTO.setResult(groupAboutItemType.get(group.getId()));
            resultList.add(envMonitorGroupDTO);
        }
        pageInfoVO.setList(resultList);
        return pageInfoVO;
    }

    List<String> getAllParameterCodeNeed(){
        EnvMonitorItemLiveParameterEnum[] values = EnvMonitorItemLiveParameterEnum.values();
        List<String> result = new ArrayList<>();
        for (EnvMonitorItemLiveParameterEnum envMonitorItemLiveParameterEnum : values) {
            result.add(envMonitorItemLiveParameterEnum.getParameterType());
        }
        return result;
    }

    public Map<Long, List<EnvMonitorItemTypeDTO>> itemTypeInfoOfGroup(List<TblItemParameter> parameterList,List<TblGroup> groupList,List<String> parameterCodeList){
        Map<Long, List<EnvMonitorItemTypeDTO>> maps = new HashMap<>();
        for (TblGroup group : groupList) {
            List<EnvMonitorItemTypeDTO> envMonitorItemTypeDTOList = new ArrayList<>();
            List<String> itemCodeList = Arrays.asList(group.getContainItemCodes().split(","));
            for (String parameterCode : parameterCodeList) {
                EnvMonitorItemTypeDTO envMonitorItemTypeDTO = new EnvMonitorItemTypeDTO();
                int num = 0;
                int totalValue = 0;
                int totalNum = 0;
                String unit = "";
                if(null != parameterList && parameterList.size()>0){
                    unit = parameterList.get(0).getUnit();
                    for (TblItemParameter itemParameter : parameterList) {
                        if(itemCodeList.contains(itemParameter.getItemCode()) && parameterCode.equals(itemParameter.getParameterType())){
                            num ++;
                            unit = itemParameter.getUnit();
                            if(null != itemParameter.getValue()){
                                totalNum ++;
                                totalValue += Integer.parseInt(itemParameter.getValue());
                            }
                        }
                    }
                }
                envMonitorItemTypeDTO.setAverageValue(percent(totalValue,totalNum,1));
                envMonitorItemTypeDTO.setItemTotalNum(num);
                envMonitorItemTypeDTO.setUnit(unit);
                envMonitorItemTypeDTO.setItemTypeCode(EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByParameterType(parameterCode).getItemTypeCode());
                envMonitorItemTypeDTO.setItemTypeName(EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByParameterType(parameterCode).getItemTypeName());
                envMonitorItemTypeDTOList.add(envMonitorItemTypeDTO);
            }
            maps.put(group.getId(),envMonitorItemTypeDTOList);
        }
        return maps;
    }

    public static String percent(double v1, double v2,int place) {
        String per = "0";
        if (v2 > 0) {
            NumberFormat percent = NumberFormat.getInstance();
            percent.setMaximumFractionDigits(place);// 小数点后几位
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
        return calendar.get(Calendar.MONTH);
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

}
