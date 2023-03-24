package com.thtf.elevator.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.github.pagehelper.PageInfo;
import com.thtf.common.constant.AlarmConstants;
import com.thtf.common.constant.ItemConstants;
import com.thtf.common.constant.ItemTypeConstants;
import com.thtf.common.dto.alarmserver.ItemAlarmNumberInfo;
import com.thtf.common.dto.alarmserver.ListAlarmInfoLimitOneParamDTO;
import com.thtf.common.dto.itemserver.*;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.entity.itemserver.TblItemType;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.elevator.config.ItemParameterConfig;
import com.thtf.elevator.dto.ParameterInfoDTO;
import com.thtf.elevator.dto.*;
import com.thtf.elevator.dto.convert.ItemConverter;
import com.thtf.elevator.dto.convert.ParameterConverter;
import com.thtf.elevator.service.ElevatorService;
import com.thtf.elevator.vo.QueryItemParamVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: liwencai
 * @Date: 2022/9/2 10:19
 * @Description:
 */
@Service("ElevatorService")
public class ElevatorServiceImpl implements ElevatorService {

    @Resource
    private ItemAPI itemAPI;

    @Resource
    private AlarmAPI alarmAPI;

    @Resource
    private AdminAPI adminAPI;

    @Resource
    private CommonService commonService;

    @Resource
    private ItemConverter itemConverter;

    @Resource
    private ItemParameterConfig itemParameterConfig;

    @Resource
    private ParameterConverter parameterConverter;


    /**
     * @Author: liwencai
     * @Description: 根据子系统编码获取设备类别
     * @Date: 2022/12/1
     * @Param sysCode: 子系统编码
     * @return: java.util.List<com.thtf.common.dto.itemserver.CodeAndNameDTO>
     */
    @Override
    public List<CodeAndNameDTO> listItemTypeLeaf(String sysCode) {
        List<CodeAndNameDTO> result;
        TblItemType tblItemType = new TblItemType();
        tblItemType.setSysCode(sysCode);
        tblItemType.setIsLeaf(ItemTypeConstants.IS_LEAF);
        List<TblItemType> itemTypeList = itemAPI.queryAllItemTypes(tblItemType).getData();
        if(CollectionUtils.isEmpty(itemTypeList)){
            return new ArrayList<>(0);
        }
        result = new ArrayList<>();
        for (TblItemType itemType : itemTypeList) {
            CodeAndNameDTO codeAndNameDTO = new CodeAndNameDTO();
            codeAndNameDTO.setCode(itemType.getCode());
            codeAndNameDTO.setName(itemType.getName());
            result.add(codeAndNameDTO);
        }
        return result;
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2023/3/8
     * @Param queryItemParamVO:
     * @Return: java.lang.Object
     */
    @Override
    public List<ElevatorInfoResultDTO> queryItem(QueryItemParamVO queryItemParamVO) {
        ListItemNestedParametersParamDTO paramDTO = new ListItemNestedParametersParamDTO();
        paramDTO.setSysCode(queryItemParamVO.getSysCode());
        if(StringUtils.isNotBlank(queryItemParamVO.getBuildingCodes())){
            paramDTO.setBuildingCodeList(Arrays.asList(queryItemParamVO.getBuildingCodes().split(",")));
        }
        if(StringUtils.isNotBlank(queryItemParamVO.getAreaCode())){
            paramDTO.setAreaCodeList(Arrays.asList(queryItemParamVO.getAreaCode().split(",")));
        }
        if (StringUtils.isNotBlank(queryItemParamVO.getItemTypeCodes())){
            paramDTO.setItemTypeCodeList(Arrays.asList(queryItemParamVO.getItemTypeCodes().split(",")));
        }

        List<ListItemNestedParametersResultDTO> data = itemAPI.listItemNestedParameters(paramDTO).getData();

        // 结果集
        List<ElevatorInfoResultDTO> resultDTOList = itemConverter.toElevatorInfoResultList(data);

        for (ElevatorInfoResultDTO elevatorInfoResultDTO : resultDTOList) {
            for (ListItemNestedParametersResultDTO item : data) {
                if (item.getItemCode().equals(elevatorInfoResultDTO.getItemCode())){
                    if(ItemConstants.ITEM_ALARM_TRUE.equals(item.getAlarm())){
                        elevatorInfoResultDTO.setAlarmCategory(AlarmConstants.ALARM_CATEGORY_INTEGER.toString());
                    }
                    if (ItemConstants.ITEM_ALARM_FALSE.equals(item.getAlarm()) && ItemConstants.ITEM_FAULT_TRUE.equals(item.getFault())) {
                        elevatorInfoResultDTO.setAlarmCategory(AlarmConstants.FAULT_CATEGORY_INTEGER.toString());
                    }
                    convertParameterPropertiesToElevatorInfoResultDTO(elevatorInfoResultDTO,item.getParameterList());
                }
            }
        }

        return resultDTOList;
    }

    /**
     * @Author: liwencai
     * @Description: 前端数据展示
     * @Date: 2022/9/5
     * @Param sysCode:
     * @Param itemType:
     * @return: java.util.List<com.thtf.elevator.dto.DisplayInfoDTO>
     */
    @Override
    public List<DisplayInfoDTO> displayInfo(String sysCode, String buildingCodes, String areaCode) {

        // 获取设备参数为运行时的状态值
        String parameterValueByStateExplain = commonService.getParameterValueByStateExplain(sysCode, itemParameterConfig.getState(), null, new String[]{"运","行","在"});

        List<String> buildingCodeList = StringUtils.isNotBlank(buildingCodes)?Arrays.asList(buildingCodes.split(",")):adminAPI.listBuildingCodeUserSelf().getData();
        List<String> areaCodeList = StringUtils.isNotBlank(areaCode)?Arrays.asList(areaCode.split(",")):null;
        List<DisplayInfoDTO> result = new ArrayList<>();
        // 获取电梯的所有子类,这里假设只有一级父级
        TblItemType tblItemType = new TblItemType();
        tblItemType.setSysCode(sysCode);
        tblItemType.setIsLeaf(ItemTypeConstants.IS_LEAF);
        // 父类为itemType的设备类别
        List<TblItemType> itemTypeList = itemAPI.queryAllItemTypes(tblItemType).getData();
        // 根据类别查询所有的信息
        for (TblItemType itemType : itemTypeList) {
            DisplayInfoDTO displayInfoDTO = new DisplayInfoDTO();
            // 查询该类的数量
            TblItem tblItem = new TblItem();
            tblItem.setSystemCode(sysCode);
            tblItem.setTypeCode(itemType .getCode());
            tblItem.setBuildingCodeList(buildingCodeList);
            tblItem.setAreaCodeList(areaCodeList);
            List<TblItem> itemList = itemAPI.queryAllItems(tblItem).getData();
            List<KeyValueDTO> kvList = new ArrayList<>();
            KeyValueDTO keyValueDTO = new KeyValueDTO();
            keyValueDTO.setKey(itemType .getName());
            keyValueDTO.setValue(itemList.size());
            kvList.add(keyValueDTO);
            // 查询该类型的运行数量
            String parameterType = itemParameterConfig.getState();
            // 运行数量
            List<String> itemCodeList = itemList.stream().map(TblItem::getCode).collect(Collectors.toList());
            KeyValueDTO runNumberKV = new KeyValueDTO();
            runNumberKV.setKey("运行总数");
            if(itemCodeList.size()>0){
                if(StringUtils.isBlank(parameterValueByStateExplain)){
                    runNumberKV.setValue(0);
                }else {
                    Integer runNumber = itemAPI.countParameterNumByItemCodeListAndPtypeAndPvalue(parameterType, itemList.stream().map(TblItem::getCode).collect(Collectors.toList()), parameterValueByStateExplain).getData();
                    runNumberKV.setValue(runNumber);
                }
            }else {
                runNumberKV.setValue(0);
            }
            kvList.add(runNumberKV);
            displayInfoDTO.setResults(kvList);
            result.add(displayInfoDTO);
        }
        return result;
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/9/5
     * @Param itemCodeList: 设备编码集
     * @Param isNeedAreaName: 是否需要区域中文名称
     * @return: java.util.List<com.thtf.elevator.dto.ElevatorInfoResultDTO>
     */
    @Override
    public List<ElevatorInfoResultDTO> listElevatorItemByCodeList(List<String> itemCodeList) {
        if(null == itemCodeList || itemCodeList.size() == 0){
            return null;
        }
        List<ElevatorInfoResultDTO> result = new ArrayList<>();
        List<TblItemParameter> allParameters = itemAPI.searchParameterByItemCodes(itemCodeList).getData();
        for (String itemCode : itemCodeList) {
            ElevatorInfoResultDTO elevatorInfoResultDTO = new ElevatorInfoResultDTO();
            // 查询设备信息
            TblItem itemInfo = itemAPI.searchItemByItemCode(itemCode).getData();
            elevatorInfoResultDTO.setItemId(itemInfo.getId());
            elevatorInfoResultDTO.setItemCode(itemInfo.getCode());
            elevatorInfoResultDTO.setItemName(itemInfo.getName());
            elevatorInfoResultDTO.setAreaCode(itemInfo.getAreaCode());
            elevatorInfoResultDTO.setAreaName(itemInfo.getAreaName());
            elevatorInfoResultDTO.setBuildingCode(itemInfo.getBuildingCode());
            Map<String, String> data = adminAPI.getBuildingMap(Collections.singletonList(itemInfo.getBuildingCode())).getData();
            elevatorInfoResultDTO.setBuildingName(data.get(itemInfo.getBuildingCode()));
            // 查询参数信息
            List<TblItemParameter> parameterList = allParameters.stream().filter(e -> e.getItemCode().equals(itemCode)).collect(Collectors.toList());
            convertParameterPropertiesToElevatorInfoResultDTO(elevatorInfoResultDTO,parameterList);
            result.add(elevatorInfoResultDTO);
        }
        return result;
    }


    /**
     * @Author: liwencai
     * @Description: 查询所有的电梯设备信息
     * @Date: 2022/9/5
     * @Param sysCode: 子系统编码
     * @Param itemTypeCode: 设备类别编码
     * @Param pageNum: 页号
     * @Param pageSize: 页大小
     * @Return: com.thtf.elevator.vo.PageInfoVO
     */
    @Override
    public PageInfo<ElevatorInfoResultDTO> listElevatorItemPage(String sysCode,String buildingCodes, String areaCode,String itemTypeCode, Integer state, Integer pageNum, Integer pageSize) {

        List<String> buildingCodesList = null;
        List<String> areaCodesList = null;
        if(StringUtils.isNotBlank(areaCode)){
            areaCodesList = Arrays.asList(areaCode.split(","));
        }else {
            if(StringUtils.isNotBlank(buildingCodes)){
                buildingCodesList = Arrays.asList(buildingCodes.split(","));
            }
        }
        ListItemNestedParametersPageParamDTO listItemPage = new ListItemNestedParametersPageParamDTO();
        listItemPage.setSysCode(sysCode);
        listItemPage.setBuildingCodeList(buildingCodesList);
        listItemPage.setAreaCodeList(areaCodesList);
        if(StringUtils.isNotBlank(itemTypeCode)){
            listItemPage.setItemTypeCodeList(Collections.singletonList(itemTypeCode));
        }
        listItemPage.setPageNumber(pageNum);
        listItemPage.setPageSize(pageSize);
        if(null != state){
            List<ParameterTypeCodeAndValueDTO> parameterList = new ArrayList<>();
            ParameterTypeCodeAndValueDTO param = new ParameterTypeCodeAndValueDTO();
            param.setParameterTypeCode(itemParameterConfig.getState());
            // 获取指定的参数的值
            String parameterValueByStateExplain;
            if(state == 0){
                // 0 离线 停止
                parameterValueByStateExplain = commonService.getParameterValueByStateExplain(sysCode, itemParameterConfig.getState(), itemTypeCode, new String[]{"离", "停", "止"});
            }else {
                // 1 在线 运行
                parameterValueByStateExplain = commonService.getParameterValueByStateExplain(sysCode, itemParameterConfig.getState(), itemTypeCode, new String[]{"在", "运", "行"});
            }
            param.setParameterValue(String.valueOf(parameterValueByStateExplain));
            param.setParameterValue(String.valueOf(state));
            parameterList.add(param);
            listItemPage.setParameterList(parameterList);
        }
        PageInfo<ItemNestedParameterVO> pageInfo = itemAPI.listItemNestedParametersPage(listItemPage).getData();

        if(null == pageInfo){
            return null;
        }
        PageInfo<ElevatorInfoResultDTO> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo,result);
        List<ItemNestedParameterVO> itemNestedParameterList = pageInfo.getList();

        // 建筑编码
        List<String> buildingCodeListForSelect = itemNestedParameterList.stream().map(ItemNestedParameterVO::getBuildingCode).collect(Collectors.toList());
        Map<String, String> buildingMap = adminAPI.getBuildingMap(buildingCodeListForSelect).getData();

        // 结果集
        List<ElevatorInfoResultDTO> resultDTOList = itemConverter.toElevatorInfoList(itemNestedParameterList);

        for (ElevatorInfoResultDTO elevatorInfoResultDTO : resultDTOList) {
            for (ItemNestedParameterVO item : pageInfo.getList()) {
                if (item.getCode().equals(elevatorInfoResultDTO.getItemCode())){
                    convertParameterPropertiesToElevatorInfoResultDTO(elevatorInfoResultDTO,item.getParameterList());
                    elevatorInfoResultDTO.setBuildingName(buildingMap.get(item.getBuildingCode()));
                    if(ItemConstants.ITEM_ALARM_TRUE.equals(item.getAlarm())){
                        elevatorInfoResultDTO.setAlarmCategory(AlarmConstants.ALARM_CATEGORY_INTEGER.toString());
                    }
                    if (ItemConstants.ITEM_ALARM_FALSE.equals(item.getAlarm()) && ItemConstants.ITEM_FAULT_TRUE.equals(item.getFault())) {
                        elevatorInfoResultDTO.setAlarmCategory(AlarmConstants.FAULT_CATEGORY_INTEGER.toString());
                    }
                }
            }
        }
        result.setList(resultDTOList);
        return result;
    }



    /**
     * @Author: liwencai
     * @Description: 查询所有的报警电梯设备
     * @Date: 2022/9/5
     * @Param sysCode: 子系统编码
     * @return: java.util.List<com.thtf.elevator.dto.ElevatorAlarmResultDTO>
     */
    @Override
    public PageInfo<ElevatorAlarmResultDTO> listElevatorAlarmPage(String sysCode,String buildingCodes, String areaCode, String itemTypeCode,Integer alarmCategory,Integer pageNumber,Integer pageSize) {
        // 故障设备信息
        List<String> buildingCodesList = null;
        List<String> areaCodesList = null;
        if(StringUtils.isNotBlank(areaCode)){
            areaCodesList = Arrays.asList(areaCode.split(","));
        }else {
            if(StringUtils.isNotBlank(buildingCodes)){
                buildingCodesList = Arrays.asList(buildingCodes.split(","));
            }
        }
        ListItemNestedParametersPageParamDTO paramDTO = new ListItemNestedParametersPageParamDTO();
        if(null != alarmCategory){
            if (AlarmConstants.ALARM_CATEGORY_INTEGER.equals(alarmCategory)){
                paramDTO.setAlarm(ItemConstants.ITEM_ALARM_TRUE);
            }
            if(AlarmConstants.FAULT_CATEGORY_INTEGER.equals(alarmCategory)){
                paramDTO.setAlarm(ItemConstants.ITEM_ALARM_FALSE);
                paramDTO.setFault(ItemConstants.ITEM_FAULT_TRUE);
            }
        }
        paramDTO.setSysCode(sysCode);
        paramDTO.setBuildingCodeList(buildingCodesList);
        paramDTO.setAreaCodeList(areaCodesList);
        if(StringUtils.isNotBlank(itemTypeCode)){
            paramDTO.setItemTypeCodeList(Collections.singletonList(itemTypeCode));
        }
        paramDTO.setPageNumber(pageNumber);
        paramDTO.setPageSize(pageSize);
        PageInfo<ItemNestedParameterVO> pageInfo = itemAPI.listItemNestedParametersPage(paramDTO).getData();
        // PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(pageInfo);
        PageInfo<ElevatorAlarmResultDTO> pageInfoVO = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo,pageInfoVO);
        List<ItemNestedParameterVO> itemList = pageInfo.getList();
        if(CollectionUtils.isEmpty(pageInfo.getList())){
            return null;
        }
        // 报警或故障的设备编码
        List<String> alarmOrFaultItemCodeList = itemList.stream().filter(e->(e.getAlarm().equals(ItemConstants.ITEM_ALARM_TRUE) || e.getFault().equals(ItemConstants.ITEM_FAULT_TRUE))).map(ItemNestedParameterVO::getCode).collect(Collectors.toList());
        List<TblAlarmRecordUnhandle> recordUnhandles = new ArrayList<>();
        if(alarmOrFaultItemCodeList.size()>0){
            /* 获取故障报警信息 */
            ListAlarmInfoLimitOneParamDTO listAlarmInfoLimitOneParamDTO = new ListAlarmInfoLimitOneParamDTO();
            // 设置筛选条件为故障报警
            listAlarmInfoLimitOneParamDTO.setItemCodeList(alarmOrFaultItemCodeList);
            recordUnhandles = alarmAPI.getAlarmInfoByItemCodeListLimitOne(alarmOrFaultItemCodeList).getData();
        }
        List<ElevatorAlarmResultDTO> resultDTOList = new ArrayList<>();
        for (ItemNestedParameterVO item : pageInfo.getList()) {
            ElevatorAlarmResultDTO elevatorInfoResult = new ElevatorAlarmResultDTO();
            elevatorInfoResult.setItemId(item.getId());
            elevatorInfoResult.setItemCode(item.getCode());
            elevatorInfoResult.setItemName(item.getName());
            elevatorInfoResult.setAreaName(item.getAreaName());
            elevatorInfoResult.setAreaCode(item.getAreaCode());
            elevatorInfoResult.setBuildingCode(item.getBuildingCode());
            if(ItemConstants.ITEM_ALARM_TRUE.equals(item.getAlarm())){
                elevatorInfoResult.setAlarmCategory(AlarmConstants.ALARM_CATEGORY_INTEGER.toString());
            }
            if (ItemConstants.ITEM_ALARM_FALSE.equals(item.getAlarm()) && ItemConstants.ITEM_FAULT_TRUE.equals(item.getFault())) {
                elevatorInfoResult.setAlarmCategory(AlarmConstants.FAULT_CATEGORY_INTEGER.toString());
            }
            // 匹配报警信息
            for (TblAlarmRecordUnhandle alarmRecordUnhandle : recordUnhandles) {
                if(item.getCode().equals(alarmRecordUnhandle.getItemCode())){
                    long duration = LocalDateTimeUtil.between(alarmRecordUnhandle.getAlarmTime(), LocalDateTime.now(), ChronoUnit.MILLIS);
                    elevatorInfoResult.setStayTime(DateUtil.formatBetween(duration, BetweenFormatter.Level.SECOND));
                    elevatorInfoResult.setAlarmLevel(alarmRecordUnhandle.getAlarmLevel());
                    elevatorInfoResult.setAlarmTime(alarmRecordUnhandle.getAlarmTime());
                }
            }
            convertParameterPropertiesToElevatorInfoResultDTO(elevatorInfoResult,item.getParameterList());
            resultDTOList.add(elevatorInfoResult);
        }
        pageInfoVO.setList(resultDTOList);
        return pageInfoVO;
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备报警、故障统计
     * @Date: 2023/1/31
     * @Param sysCode: 子系统编码
     * @Param startTime: 开始时间
     * @Param endTime: 结束时间
     * @Return: java.util.List<com.thtf.common.dto.alarmserver.ItemAlarmNumberInfo>
     */
    @Override
    public ItemFaultStatisticsDTO getItemFaultStatistics(String sysCode, String buildingCodes,String areaCode,String itemTypeCode,String startTime,String endTime) {

        List<ItemAlarmNumberInfo> itemFaultStatistics = alarmAPI.getAlarmNumberByStartAndEndTime(sysCode, buildingCodes,areaCode,itemTypeCode, startTime, endTime).getData();

        if(null == itemFaultStatistics || itemFaultStatistics.size() == 0){
            return null;
        }
        ItemFaultStatisticsDTO result = new ItemFaultStatisticsDTO();
        List<String> itemNameList = new ArrayList<>();
        List<Integer> monitorAlarmNumberList = new ArrayList<>();
        List<Integer> malfunctionAlarmNumberList = new ArrayList<>();
        for (ItemAlarmNumberInfo alarmNumberInfo : itemFaultStatistics) {
            itemNameList.add(alarmNumberInfo.getItemName());
            monitorAlarmNumberList.add(alarmNumberInfo.getMonitorAlarmNumber());
            malfunctionAlarmNumberList.add(alarmNumberInfo.getMalfunctionAlarmNumber());
        }
        result.setItemName(itemNameList);
        result.setMonitorAlarmNumber(monitorAlarmNumberList);
        result.setMalfunctionAlarmNumber(malfunctionAlarmNumberList);
        return result;
    }


    /**
     * @Author: liwencai
     * @Description: 将参数映射到ElevatorInfoResultDTO属性中
     * @Date: 2022/11/29
     * @Param elevatorInfoResultDTO:
     * @Param parameterList:
     * @return: void
     */
    public void convertParameterPropertiesToElevatorInfoResultDTO(ElevatorInfoResultDTO elevatorInfoResultDTO, List<TblItemParameter> parameterList){
        // 匹配参数信息
        List<ParameterInfoDTO> parameterInnerList = new ArrayList<>();
        for (TblItemParameter parameter : parameterList) {
            // 下行
            if(itemParameterConfig.getDownGoing().equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setDownGoingParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 上行
            if(itemParameterConfig.getUpGoing().equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setUpGoingParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 运行时长
            if(itemParameterConfig.getRunTime().equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setRunTimeParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 报警状态
            if(itemParameterConfig.getAlarm().equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setAlarmParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 故障状态
            if(itemParameterConfig.getFault().equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setFaultParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 锁梯状态
            if(itemParameterConfig.getLockStatus().equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setLockStatusParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 当前楼层
            if(itemParameterConfig.getCurrentFloor().equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setCurrentFloorValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 运行状态
            if(itemParameterConfig.getState().equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setRunParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 是否超载
            if(itemParameterConfig.getOverLoad().equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setOverLoadParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
        }
        elevatorInfoResultDTO.setParameterList(parameterInnerList);
    }
}
