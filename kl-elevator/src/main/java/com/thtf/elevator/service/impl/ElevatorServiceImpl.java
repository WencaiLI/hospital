package com.thtf.elevator.service.impl;

import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.alarmserver.ItemAlarmNumberInfo;
import com.thtf.common.dto.alarmserver.ListAlarmInfoLimitOneParamDTO;
import com.thtf.common.dto.itemserver.CountItemByParameterListDTO;
import com.thtf.common.dto.itemserver.ItemNestedParameterVO;
import com.thtf.common.dto.itemserver.TblItemDTO;
import com.thtf.common.entity.adminserver.TblBuildingArea;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.entity.itemserver.TblItemType;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.elevator.common.enums.ParameterConstant;
import com.thtf.elevator.dto.*;
import com.thtf.elevator.dto.convert.FloorConverter;
import com.thtf.elevator.dto.convert.ItemConverter;
import com.thtf.elevator.dto.convert.PageInfoConvert;
import com.thtf.elevator.dto.convert.ParameterConverter;
import com.thtf.elevator.service.ElevatorService;
import com.thtf.elevator.vo.PageInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.docx4j.wml.Tbl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        // 父类为itemType的设备类别
        List<TblItemType> itemTypeList = itemAPI.queryAllItemTypes(tblItemType).getData();
        // 移除父类
        if(null == itemTypeList || itemTypeList.size() == 0){
            return null;
        }
        itemTypeList.removeIf(e->("item".equals(e.getParentCode()) || StringUtils.isBlank(e.getParentCode())));
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
            // todo 此处不明确，最好使用常量标识运行参数的类型编码
            String parameterType = "OnlineStatus";
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
        param.setParameterTypeCode("Alarm");
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
        param.setParameterTypeCode("Alarm");
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
     * @Param sysCode:
     * @return: java.util.List<com.thtf.elevator.dto.ElevatorInfoResultDTO>
     */
    @Override
    public PageInfoVO getAllElevatorPage(String sysCode,String itemTypeCode,Integer pageNum, Integer pageSize) {

        // 设备信息
        List<String> itemCodeList = new ArrayList<>();
        PageInfo<ItemNestedParameterVO> itemInfosPage;
        if(StringUtils.isNotBlank(itemTypeCode)){
            itemCodeList.add(itemTypeCode);
            itemInfosPage = itemAPI.searchItemNestedParametersBySyscodeAndItemTypeCodePage(sysCode,itemCodeList,pageNum,pageSize).getData();
        }else {
            itemInfosPage = itemAPI.searchItemNestedParametersBySyscodeAndItemTypeCodePage(sysCode,null,pageNum,pageSize).getData();
        }

        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(itemInfosPage);

        List<ElevatorInfoResultDTO> resultDTOList = itemConverter.toElevatorInfoList(itemInfosPage.getList());

        for (ElevatorInfoResultDTO elevatorInfoResultDTO : resultDTOList) {
            for (ItemNestedParameterVO item : itemInfosPage.getList()) {
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
                // elevatorInfoResultDTO.setDownGoingParameterCode(parameter.getCode());
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setDownGoingParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 上行
            if(ParameterConstant.ELEVATOR_UP_GOING_STATUS.equals(parameter.getParameterType())){
                // elevatorInfoResultDTO.setUpGoingParameterCode(parameter.getCode());
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setUpGoingParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 运行时长
            if(ParameterConstant.ELEVATOR_RUN_TIME.equals(parameter.getParameterType())){
                // elevatorInfoResultDTO.setRunTimeParameterCode(parameter.getCode());
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setRunTimeParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 报警状态
            if(ParameterConstant.ELEVATOR_ALARM.equals(parameter.getParameterType())){
                // elevatorInfoResultDTO.setAlarmParameterCode(parameter.getCode());
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setAlarmParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 故障状态
            if(ParameterConstant.ELEVATOR_FAULT.equals(parameter.getParameterType())){
                // elevatorInfoResultDTO.setFaultParameterCode(parameter.getCode());
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setFaultParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 锁梯状态
            if(ParameterConstant.ELEVATOR_LOCK_STATUS.equals(parameter.getParameterType())){
                // elevatorInfoResultDTO.setLockStatusParameterCode(parameter.getCode());
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setLockStatusParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 当前楼层
            if(ParameterConstant.ELEVATOR_CURRENT_FLOOR.equals(parameter.getParameterType())){
                // elevatorInfoResultDTO.setCurrentFloorCode(parameter.getCode());
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setCurrentFloorValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }

            // 当前楼层
            if(ParameterConstant.ELEVATOR_RUN_STATUS.equals(parameter.getParameterType())){
                // elevatorInfoResultDTO.setRunParameterCode(parameter.getCode());
                if(StringUtils.isNotBlank(parameter.getValue())){
                    elevatorInfoResultDTO.setRunTimeParameterValue(parameter.getValue()+Optional.ofNullable(parameter.getUnit()).orElse(""));
                }
                parameterInnerList.add(parameterConverter.toParameterInfo(parameter));
            }
            // 是否超载
            if(ParameterConstant.ELEVATOR_OVERLOAD.equals(parameter.getParameterType())){
                // elevatorInfoResultDTO.setOverLoadParameterCode(parameter.getCode());
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
    public PageInfoVO getAllAlarmPage(String sysCode,String itemTypeCode,Integer pageNumber,Integer pageSize) {
//        // 设备信息
//        List<String> itemTypeCodeList = new ArrayList<>();
//        itemTypeCodeList.add(itemTypeCode);
        PageInfo<TblAlarmRecordUnhandle> alarmPageInfo;
//        if(StringUtils.isNotBlank(itemTypeCode)){
//            alarmPageInfo = alarmAPI.getAlarmInfoBySysCodeAndItemTypeCodeLimitOnePage(sysCode,itemTypeCodeList, pageNumber, pageSize).getData();
//            // alarmAPI.getAlarmInfoBySysCodeLimitOne()
//
//        }else {
//            alarmPageInfo = alarmAPI.getAlarmInfoBySysCodeAndItemTypeCodeLimitOnePage(sysCode,null, pageNumber, pageSize).getData();
//        }
        /* 获取故障报警信息 */
        ListAlarmInfoLimitOneParamDTO listAlarmInfoLimitOneParamDTO = new ListAlarmInfoLimitOneParamDTO();
        listAlarmInfoLimitOneParamDTO.setSystemCode(sysCode);
        // 设置筛选条件为故障报警
        listAlarmInfoLimitOneParamDTO.setAlarmCategory("1");
        listAlarmInfoLimitOneParamDTO.setItemTypeCode(itemTypeCode);
        listAlarmInfoLimitOneParamDTO.setPageNumber(pageNumber);
        listAlarmInfoLimitOneParamDTO.setPageSize(pageSize);
        alarmPageInfo = alarmAPI.listAlarmInfoLimitOnePage(listAlarmInfoLimitOneParamDTO).getData();

        System.out.println(alarmPageInfo);
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
            System.out.println(recordUnhandles);
            for (TblAlarmRecordUnhandle alarmRecordUnhandle : recordUnhandles) {
                if(item.getCode().equals(alarmRecordUnhandle.getItemCode())){
                    elevatorInfoResult.setStayTime(getTimeGap(alarmRecordUnhandle.getAlarmTime(),LocalDateTime.now()));
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


        // return resultMap;
    }

    @Override
    public List<ItemAlarmNumberInfo> getItemFaultStatistics(String sysCode,String startTime,String endTime) {
//        List<KeyValueDTO> result = new ArrayList<>();
//        // 查询所有设备信息
//        List<ItemNestedParameterVO> itemInfos = itemAPI.searchItemNestedParametersBySyscode(sysCode).getData();
//        for (ItemNestedParameterVO itemNestedParameterVO : itemInfos) {
//            KeyValueDTO keyValueDTO = new KeyValueDTO();
//            keyValueDTO.setKey(itemNestedParameterVO.getName());
//            TblAlarmRecordUnhandle recordUnhandle = new TblAlarmRecordUnhandle();
//            recordUnhandle.setSystemCode(sysCode);
//            recordUnhandle.setItemCode(itemNestedParameterVO.getCode());
//            keyValueDTO.setValue(alarmAPI.queryAllAlarmCount(recordUnhandle));
//            result.add(keyValueDTO);
//        }

        return alarmAPI.getAlarmNumberByStartAndEndTime(sysCode, null, startTime, endTime).getData();
    }

    /* ================= 复用代码区 =================== */
    Map<String, Object> getMapFromPageInfo(PageInfo pageInfo){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("size",pageInfo.getSize());
        resultMap.put("total",pageInfo.getTotal());
        resultMap.put("endRow",pageInfo.getEndRow());
        resultMap.put("navigateFirstPage",pageInfo.getNavigateFirstPage());
        resultMap.put("navigateLastPage",pageInfo.getNavigateLastPage());
        resultMap.put("navigatepageNums",pageInfo.getNavigatepageNums());
        resultMap.put("navigatePages",pageInfo.getNavigatePages());
        resultMap.put("nextPage",pageInfo.getNextPage());
        resultMap.put("startRow",pageInfo.getStartRow());
        resultMap.put("prePage",pageInfo.getPrePage());
        resultMap.put("pages",pageInfo.getPages());
        resultMap.put("pageNum",pageInfo.getPageNum());
        resultMap.put("pageSize",pageInfo.getPageSize());
        return resultMap;
    }

    /**
     * @Author: liwencai
     * @Description: 获取时间差（*天*小时*分*秒）
     * @Date: 2022/10/27
     * @Param: startTime: 开始时间
     * @Param: endTime: 结束使劲按
     * @Return: java.lang.String
     */
    public String getTimeGap(LocalDateTime startTime, LocalDateTime endTime){
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
}
