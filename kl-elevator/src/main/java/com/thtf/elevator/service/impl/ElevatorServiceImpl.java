package com.thtf.elevator.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.alarmserver.ItemAlarmNumberInfo;
import com.thtf.common.dto.alarmserver.ListAlarmInfoLimitOneParamDTO;
import com.thtf.common.dto.itemserver.*;
import com.thtf.common.entity.adminserver.TblBuildingArea;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.entity.itemserver.TblItemType;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.elevator.common.constant.ItemTypeConstant;
import com.thtf.elevator.common.constant.ParameterConstant;
import com.thtf.elevator.dto.*;
import com.thtf.elevator.dto.ParameterInfoDTO;
import com.thtf.elevator.dto.convert.FloorConverter;
import com.thtf.elevator.dto.convert.ItemConverter;
import com.thtf.elevator.dto.convert.PageInfoConvert;
import com.thtf.elevator.dto.convert.ParameterConverter;
import com.thtf.elevator.service.ElevatorService;
import com.thtf.elevator.vo.PageInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    ItemConverter itemConverter;

    @Resource
    FloorConverter floorConverter;

    @Resource
    PageInfoConvert pageInfoConvert;

    @Resource
    ParameterConverter parameterConverter;


    /**
     * @Author: liwencai
     * @Description: 获取楼层信息
     * @Date: 2022/10/8
     * @Return: java.util.List<com.thtf.elevator.dto.FloorInfoDTO>
     */
    @Override
    public List<FloorInfoDTO> getFloorInfo(String buildingCode,String systemCode){
        List<TblBuildingArea> resourceList = adminAPI.getFloorInfo(buildingCode,systemCode).getData();
        return floorConverter.toFloorList(resourceList);
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/12/1
     * @Param sysCode:
     * @return: java.util.List<com.thtf.common.dto.itemserver.CodeAndNameDTO>
     */
    @Override
    public List<CodeAndNameDTO> getItemType(String sysCode) {
        List<CodeAndNameDTO> result;
        TblItemType tblItemType = new TblItemType();
        tblItemType.setSysCode(sysCode);
        tblItemType.setIsLeaf(ItemTypeConstant.IS_LEAF);
        List<TblItemType> itemTypeList = itemAPI.queryAllItemTypes(tblItemType).getData();
        if(null == itemTypeList || itemTypeList.size() == 0){
            return null;
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
     * @Description: 前端数据展示
     * @Date: 2022/9/5
     * @Param sysCode:
     * @Param itemType:
     * @return: java.util.List<com.thtf.elevator.dto.DisplayInfoDTO>
     */
    @Override
    public List<DisplayInfoDTO> displayInfo(String sysCode) {
        List<DisplayInfoDTO> result = new ArrayList<>();
        // 获取电梯的所有子类,这里假设只有一级父级
        TblItemType tblItemType = new TblItemType();
        tblItemType.setSysCode(sysCode);
        tblItemType.setIsLeaf(ItemTypeConstant.IS_LEAF);
        // 父类为itemType的设备类别
        List<TblItemType> itemTypeList = itemAPI.queryAllItemTypes(tblItemType).getData();
        // 根据类别查询所有的信息
        for (TblItemType itemType : itemTypeList) {
            DisplayInfoDTO displayInfoDTO = new DisplayInfoDTO();
            // 查询该类的数量
            TblItem tblItem = new TblItem();
            tblItem.setSystemCode(sysCode);
            tblItem.setTypeCode(itemType .getCode());
            List<TblItem> itemList = itemAPI.queryAllItems(tblItem).getData();
            List<KeyValueDTO> kvList = new ArrayList<>();
            KeyValueDTO keyValueDTO = new KeyValueDTO();
            keyValueDTO.setKey(itemType .getName());
            keyValueDTO.setValue(itemList.size());
            kvList.add(keyValueDTO);
            // 查询该类型的运行数量
            String parameterType = ParameterConstant.ELEVATOR_RUN_STATUS;
            String parameterValue = "1";
            // 运行数量
            Integer runNumber = itemAPI.countParameterNumByItemCodeListAndPtypeAndPvalue(
                    parameterType,
                    itemList.stream().map(TblItem::getCode).collect(Collectors.toList()),
                    parameterValue).getData();

            KeyValueDTO runNumberKV = new KeyValueDTO();
            runNumberKV.setKey("运行总数");
            runNumberKV.setValue(runNumber);
            kvList.add(runNumberKV);
            displayInfoDTO.setResults(kvList);
            result.add(displayInfoDTO);
        }
        // 报警总数
        KeyValueDTO alarmKV = new KeyValueDTO();
        alarmKV.setKey("故障报警");
        CountItemByParameterListDTO param = new CountItemByParameterListDTO();
        param.setSysCode(sysCode);
        param.setParameterTypeCode(ParameterConstant.ELEVATOR_FAULT);
        param.setParameterValue("1");
        alarmKV.setValue(itemAPI.countItemByParameterList(param).getData());
        DisplayInfoDTO displayInfoDTO = new DisplayInfoDTO();
        displayInfoDTO.setResults(new ArrayList<KeyValueDTO>(Collections.singleton(alarmKV)));
        result.add(displayInfoDTO);
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 获取 “故障” 报警数量
     * @Date: 2022/9/5
     * @Param sysCode: 子系统编码
     * @return: java.lang.Integer
     */
    @Override
    public Integer alarmNumber(String sysCode) {
        CountItemByParameterListDTO param = new CountItemByParameterListDTO();
        param.setSysCode(sysCode);
        param.setParameterTypeCode(ParameterConstant.ELEVATOR_FAULT);
        param.setParameterValue("1");
        return itemAPI.countItemByParameterList(param).getData();
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
    public List<ElevatorInfoResultDTO> itemCodeList(List<String> itemCodeList,Boolean isNeedAreaName) {
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
            if(isNeedAreaName){
                elevatorInfoResultDTO.setAreaName(adminAPI.searchAreaByCode(itemInfo.getAreaCode()).getData().getName());
            }
            // 查询参数信息
            List<TblItemParameter> parameterList = allParameters.stream().filter(e -> e.getItemCode().equals(itemCode)).collect(Collectors.toList());
            convertParameterPropertiesToElevatorInfoResultDTO(elevatorInfoResultDTO,parameterList);
            result.add(elevatorInfoResultDTO);
        }
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 查找item关联设备信息
     * @Date: 2022/9/5
     * @Param relationType: 关联设备类别
     * @Param itemCode: 设备类型
     * @return: java.util.List<com.thtf.common.entity.itemserver.TblItem>
     */
    @Override
    public List<TblItem> getItemRelInfo(String relationType, String itemCode) {
        // 查找到关联id
        TblItem tblItem = itemAPI.searchItemByItemCode(itemCode).getData();
        String relationItemCode = tblItem.getRelationItemCode();
        String[] itemCodeList = relationItemCode.split(",");
        // 查询设备集合
        List<TblItemDTO> tblItemDTOS = itemAPI.searchItemByItemCodes(Arrays.asList(itemCodeList)).getData();
        // 筛选设备集
        if(null == tblItemDTOS){
            return null;
        }
        tblItemDTOS.removeIf(e->! e.getTypeCode().equals(relationType));
        return itemConverter.toItemList(tblItemDTOS);
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
    public PageInfoVO getAllElevatorPage(String sysCode,String itemTypeCode, Integer state, Integer pageNum, Integer pageSize) {
        ListItemNestedParametersPageParamDTO listItemPage = new ListItemNestedParametersPageParamDTO();
        listItemPage.setSysCode(sysCode);
        listItemPage.setPageNumber(pageNum);
        listItemPage.setPageSize(pageSize);
        if(StringUtils.isNotBlank(itemTypeCode)){
            listItemPage.setItemTypeCodeList(Collections.singletonList(itemTypeCode));
        }
        if(null != state){
            List<ParameterTypeCodeAndValueDTO> parameterList = new ArrayList<>();
            ParameterTypeCodeAndValueDTO param = new ParameterTypeCodeAndValueDTO();
            param.setParameterTypeCode(ParameterConstant.ELEVATOR_RUN_STATUS);
            param.setParameterValue(String.valueOf(state));
            parameterList.add(param);
            listItemPage.setParameterList(parameterList);
        }
        PageInfo<ItemNestedParameterVO> pageInfo = itemAPI.listItemNestedParametersPage(listItemPage).getData();

        if(null == pageInfo){
            return null;
        }
        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(pageInfo);

        List<ItemNestedParameterVO> itemNestedParameterList = pageInfo.getList();

        // 结果集
        List<ElevatorInfoResultDTO> resultDTOList = itemConverter.toElevatorInfoList(itemNestedParameterList);

        for (ElevatorInfoResultDTO elevatorInfoResultDTO : resultDTOList) {
            for (ItemNestedParameterVO item : pageInfo.getList()) {
                if (item.getCode().equals(elevatorInfoResultDTO.getItemCode())){
                    convertParameterPropertiesToElevatorInfoResultDTO(elevatorInfoResultDTO,item.getParameterList());
                }
            }
        }
        for (ElevatorInfoResultDTO elevatorInfoResultDTO : resultDTOList) {
            for (ItemNestedParameterVO item : pageInfo.getList()) {
                if (item.getCode().equals(elevatorInfoResultDTO.getItemCode())){
                    convertParameterPropertiesToElevatorInfoResultDTO(elevatorInfoResultDTO,item.getParameterList());
                }
            }
        }
        pageInfoVO.setList(resultDTOList);
        return pageInfoVO;
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
            if(ParameterConstant.ELEVATOR_DOWN_GOING_STATUS.equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setDownGoingParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 上行
            if(ParameterConstant.ELEVATOR_UP_GOING_STATUS.equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setUpGoingParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 运行时长
            if(ParameterConstant.ELEVATOR_RUN_TIME.equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setRunTimeParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 报警状态
            if(ParameterConstant.ELEVATOR_ALARM.equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setAlarmParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 故障状态
            if(ParameterConstant.ELEVATOR_FAULT.equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setFaultParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 锁梯状态
            if(ParameterConstant.ELEVATOR_LOCK_STATUS.equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setLockStatusParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 当前楼层
            if(ParameterConstant.ELEVATOR_CURRENT_FLOOR.equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setCurrentFloorValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 当前楼层
            if(ParameterConstant.ELEVATOR_RUN_STATUS.equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setRunParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 是否超载
            if(ParameterConstant.ELEVATOR_OVERLOAD.equals(parameter.getParameterType())){
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setOverLoadParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
        }
        elevatorInfoResultDTO.setParameterList(parameterInnerList);
    }

    /**
     * @Author: liwencai
     * @Description: 查询所有的报警电梯设备
     * @Date: 2022/9/5
     * @Param sysCode: 子系统编码
     * @return: java.util.List<com.thtf.elevator.dto.ElevatorAlarmResultDTO>
     */
    @Override
    public PageInfoVO getAllAlarmPage(String sysCode,String itemTypeCode,Integer alarmCategory,Integer pageNumber,Integer pageSize) {
        // 故障设备信息
        TblItem tblItem = new TblItem();
        tblItem.setSystemCode(sysCode);
        tblItem.setTypeCode(itemTypeCode);
        if(null != alarmCategory){
            if (alarmCategory == 0){
                tblItem.setAlarm(1);
            }
            if(alarmCategory == 1){
                tblItem.setAlarm(0);
                tblItem.setFault(1);
            }
        }
        List<TblItem> itemList = itemAPI.queryAllItems(tblItem).getData();
        if(itemList ==  null || itemList.size() == 0){
            return null;
        }
        List<String> itemCodeList = itemList.stream().map(TblItem::getCode).collect(Collectors.toList());
        /* 获取故障报警信息 */
        PageInfo<TblAlarmRecordUnhandle> alarmPageInfo;
        ListAlarmInfoLimitOneParamDTO listAlarmInfoLimitOneParamDTO = new ListAlarmInfoLimitOneParamDTO();
        listAlarmInfoLimitOneParamDTO.setSystemCode(sysCode);
        // 设置筛选条件为故障报警
        if(null != alarmCategory){
            if (alarmCategory == 0){
                listAlarmInfoLimitOneParamDTO.setAlarmCategory("0");
            }
            if(alarmCategory == 1){
                listAlarmInfoLimitOneParamDTO.setAlarmCategory("1");
            }
        }
        if(itemCodeList.size() > 0){
            listAlarmInfoLimitOneParamDTO.setItemCodeList(itemCodeList);
        }
        listAlarmInfoLimitOneParamDTO.setPageNumber(pageNumber);
        listAlarmInfoLimitOneParamDTO.setPageSize(pageSize);
        alarmPageInfo = alarmAPI.listAlarmInfoLimitOnePage(listAlarmInfoLimitOneParamDTO).getData();
        // 报警信息
        List<TblAlarmRecordUnhandle> recordUnhandles = alarmPageInfo.getList();
        List<String> alarmItemCodeList = recordUnhandles.stream().map(TblAlarmRecordUnhandle::getItemCode).collect(Collectors.toList());
        List<ItemNestedParameterVO> itemInfos = itemAPI.searchItemNestedParametersBySysCodeAndItemCodeList(sysCode,alarmItemCodeList).getData();
        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(alarmPageInfo);
        List<ElevatorAlarmResultDTO> resultDTOList = new ArrayList<>();
        for (ItemNestedParameterVO item : itemInfos) {
            ElevatorAlarmResultDTO elevatorInfoResult = new ElevatorAlarmResultDTO();
            elevatorInfoResult.setItemId(item.getId());
            elevatorInfoResult.setItemCode(item.getCode());
            elevatorInfoResult.setItemName(item.getName());
            elevatorInfoResult.setAreaName(item.getAreaName());
            // 匹配报警信息
            for (TblAlarmRecordUnhandle alarmRecordUnhandle : recordUnhandles) {
                if(item.getCode().equals(alarmRecordUnhandle.getItemCode())){
                    long duration = LocalDateTimeUtil.between(alarmRecordUnhandle.getAlarmTime(), LocalDateTime.now(), ChronoUnit.MILLIS);
                    elevatorInfoResult.setStayTime(DateUtil.formatBetween(duration, BetweenFormatter.Level.SECOND));
                    elevatorInfoResult.setAlarmLevel(alarmRecordUnhandle.getAlarmLevel());
                    elevatorInfoResult.setAlarmTime(alarmRecordUnhandle.getAlarmTime());
                    elevatorInfoResult.setAlarmCategory(alarmRecordUnhandle.getAlarmCategory());
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
    public List<ItemAlarmNumberInfo> getItemFaultStatistics(String sysCode,String startTime,String endTime) {
        return alarmAPI.getAlarmNumberByStartAndEndTime(sysCode, null, startTime, endTime).getData();
    }
}
