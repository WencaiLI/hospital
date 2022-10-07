package com.thtf.elevator.service.impl;

import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.alarmserver.ItemAlarmNumberInfo;
import com.thtf.common.dto.itemserver.ItemNestedParameterVO;
import com.thtf.common.dto.itemserver.TblItemDTO;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemType;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.elevator.dto.DisplayInfoDTO;
import com.thtf.elevator.dto.ElevatorInfoResultDTO;
import com.thtf.elevator.dto.KeyValueDTO;
import com.thtf.elevator.dto.convert.ItemConverter;
import com.thtf.elevator.service.ElevatorService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    /**
     * @Author: liwencai
     * @Description: 前端数据展示
     * @Date: 2022/9/5
     * @Param sysCode:
     * @Param itemType:
     * @return: java.util.List<com.thtf.elevator.dto.DisplayInfoDTO>
     */
    @Override
    public List<DisplayInfoDTO> displayInfo(String sysCode, String itemType) {
        List<DisplayInfoDTO> result = new ArrayList<>();
        // 获取电梯的所有子类,这里假设只有一级父级
        TblItemType tblItemType = new TblItemType();
        // tblItemType.setParentCode(itemType);
        tblItemType.setSysCode(sysCode);
        // 父类为itemType的设备类别
        tblItemType.setParentCode(itemType);
        List<TblItemType> itemTypeList = itemAPI.queryAllItemTypes(tblItemType).getData();
        // 根据类别查询所有的信息
        for (TblItemType item_type: itemTypeList) {
            DisplayInfoDTO displayInfoDTO = new DisplayInfoDTO();

            // 查询该类的数量
            TblItem tblItem = new TblItem();
            tblItem.setSystemCode(sysCode);
            tblItem.setTypeCode(item_type.getCode());
            List<TblItem> itemList = itemAPI.queryAllItems(tblItem).getData();
            List<KeyValueDTO> kvList = new ArrayList<>();
            KeyValueDTO keyValueDTO = new KeyValueDTO();
            keyValueDTO.setKey(item_type.getName());
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
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 获取 “故障” 报警数量
     * @Date: 2022/9/5
     * @Param sysCode:
     * @return: java.lang.Integer
     */
    @Override
    public Integer alarmNumber(String sysCode) {
        TblAlarmRecordUnhandle tblAlarmRecordUnhandle = new TblAlarmRecordUnhandle();
        tblAlarmRecordUnhandle.setSystemCode(sysCode);
        tblAlarmRecordUnhandle.setAlarmCategory(1);
        return alarmAPI.queryAllAlarmCount(tblAlarmRecordUnhandle).getData();
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/9/5
     * @Param itemCodeList:
     * @Param isNeedAreaName: 是否需要区域中文名称
     * @return: java.util.List<com.thtf.elevator.dto.ElevatorInfoResultDTO>
     */
    @Override
    public List<ElevatorInfoResultDTO> itemCodeList(List<String> itemCodeList,Boolean isNeedAreaName) {
        List<ElevatorInfoResultDTO> result = new ArrayList<>();
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
            elevatorInfoResultDTO.setParameterList(itemAPI.searchParameterByItemCodes(Collections.singletonList(itemCode)).getData());
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
    public PageInfo<ItemNestedParameterVO> getAllElevatorPage(String sysCode,String itemTypeCode,Integer pageNum, Integer pageSize) {
        // 设备信息
        // List<ItemNestedParameterVO> itemInfos = itemAPI.searchItemNestedParametersBySyscode(sysCode).getData();
        // PageInfo<ItemNestedParameterVO> itemInfosPage = itemAPI.searchItemNestedParametersBySyscodePage(sysCode,pageNum,pageSize).getData();
        List<String> itemCodeList = new ArrayList<>();
        PageInfo<ItemNestedParameterVO> itemInfosPage;
        if(StringUtils.isNotBlank(itemTypeCode)){
            itemCodeList.add(itemTypeCode);
            itemInfosPage = itemAPI.searchItemNestedParametersBySyscodeAndItemTypeCodePage(sysCode,itemCodeList,pageNum,pageSize).getData();
        }else {
            itemInfosPage = itemAPI.searchItemNestedParametersBySyscodeAndItemTypeCodePage(sysCode,null,pageNum,pageSize).getData();
        }
        // todo PageInfo<ItemNestedParameterVO> itemInfosPage = itemAPI.searchItemNestedParametersByConditionPage();
        // 查询报警信息
        List<TblAlarmRecordUnhandle> recordUnhandles = alarmAPI.getAlarmInfoBySysCodeLimitOne(sysCode).getData();

        // todo 此处有优化空间
        for (ItemNestedParameterVO item : itemInfosPage.getList()) {
            item.setAreaName(adminAPI.searchAreaByCode(item.getAreaCode()).getData().getName());
        }

        for (TblAlarmRecordUnhandle alarmRecordUnhandle : recordUnhandles) {
            itemInfosPage.getList().forEach(e->{
                if(e.getCode().equals(alarmRecordUnhandle.getItemCode())){
                    e.setAlarmId(alarmRecordUnhandle.getId());
                    e.setAlarmLevel(alarmRecordUnhandle.getAlarmLevel());
                    e.setAlarmStatus(alarmRecordUnhandle.getAlarmCategory());
                    e.setAlarmTime(alarmRecordUnhandle.getAlarmTime());
                }
            });
        }
        return itemInfosPage;
    }

    /**
     * @Author: liwencai
     * @Description: 查询所有的报警电梯设备
     * @Date: 2022/9/5
     * @Param sysCode: 子系统编码
     * @return: java.util.List<com.thtf.elevator.dto.ElevatorAlarmResultDTO>
     */
    @Override
    public  Map<String, Object> getAllAlarmPage(String sysCode,String itemTypeCode,Integer pageNumber,Integer pageSize) {
        Map<String, Object> resultMap = new HashMap<>();


        // 设备信息
//        List<ItemNestedParameterVO> itemInfos = itemAPI.searchItemNestedParametersBySyscode(sysCode).getData();
        List<String> itemTypeCodeList = new ArrayList<>();
        itemTypeCodeList.add(itemTypeCode);
        PageInfo<TblAlarmRecordUnhandle> alarmPageInfo;
        if(StringUtils.isNotBlank(itemTypeCode)){
            alarmPageInfo = alarmAPI.getAlarmInfoBySysCodeAndItemTypeCodeLimitOnePage(sysCode,itemTypeCodeList, pageNumber, pageSize).getData();
        }else {
            alarmPageInfo = alarmAPI.getAlarmInfoBySysCodeAndItemTypeCodeLimitOnePage(sysCode,null, pageNumber, pageSize).getData();
        }
        List<TblAlarmRecordUnhandle> recordUnhandles = alarmPageInfo.getList();
        resultMap = getMapFromPageInfo(alarmPageInfo);
        List<String> alarmItemCodeList = recordUnhandles.stream().map(TblAlarmRecordUnhandle::getItemCode).collect(Collectors.toList());
        List<ItemNestedParameterVO> itemInfos = itemAPI.searchItemNestedParametersBySysCodeAndItemCodeList(sysCode,alarmItemCodeList).getData();

        itemInfos.removeIf(e->!alarmItemCodeList.contains(e.getCode()));

        // todo 此处有优化空间
        for (ItemNestedParameterVO item : itemInfos) {
            item.setAreaName(adminAPI.searchAreaByCode(item.getAreaCode()).getData().getName());
        }

        for (TblAlarmRecordUnhandle alarmRecordUnhandle : recordUnhandles) {
            itemInfos.forEach(e->{
                if(e.getCode().equals(alarmRecordUnhandle.getItemCode())){
                    e.setAlarmId(alarmRecordUnhandle.getId());
                    e.setAlarmLevel(alarmRecordUnhandle.getAlarmLevel());
                    e.setAlarmStatus(alarmRecordUnhandle.getAlarmCategory());
                    e.setAlarmTime(alarmRecordUnhandle.getAlarmTime());
                }
            });
        }
        resultMap.put("list",itemInfos);
        return resultMap;
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
}
