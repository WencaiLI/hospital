package com.thtf.environment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.alarmserver.ItemAlarmInfoDTO;
import com.thtf.common.dto.itemserver.*;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblGroup;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.entity.itemserver.TblItemType;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.common.util.ArithUtil;
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
import org.checkerframework.checker.units.qual.min;
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
    private TblHistoryMomentMapper tblHistoryMomentMapper;

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
    private ItemParameterConvert itemParameterConvert;

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

        List<String> keys = new ArrayList<>();
        List<String> collect = initList.stream().map(ItemAlarmInfoDTO::getAttribute).map(Object::toString).collect(Collectors.toList());
        collect.forEach(e->{
            keys.add(EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(e).getParameterTypeName());
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
        return itemTypeConvert.toCodeNameVO(itemAPI.getItemTypesBySysId(sysCode).getBody().getData());
    }

    /**
     * @Author: liwencai
     * @Description: 获取设备信息
     * @Date: 2022/10/27
     * @Param: paramVO:
     * @Return: java.util.List<com.thtf.environment.vo.EnvMonitorItemResultVO>
     */
    // @Override
    public PageInfoVO listItemInfoOld(EnvMonitorItemParamVO paramVO) {
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
                // envMonitorItemResultVO.setParameterList(inner);
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
                // envMonitorItemResultVO.setParameterList(inner);
                resultVOList.add(envMonitorItemResultVO);
            }
            pageInfoVO.setList(resultVOList);
            return pageInfoVO;
        }
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
        List<EnvMonitorItemResultVO> resultVOList = new ArrayList<>();
        List<String> itemCodeList;
        List<String> groupIdStringList;
        PageInfo<TblAlarmRecordUnhandle> pageInfo = alarmAPI.getAlarmInfoBySysCodeAndCategoryLimitOneByKeywordPage(paramVO.getKeyword(), paramVO.getAlarmCategory(), paramVO.getSysCode(), paramVO.getPageNumber(), paramVO.getPageSize()).getData();
        // 设备编码集
        itemCodeList = pageInfo.getList().stream().map(TblAlarmRecordUnhandle::getItemCode).collect(Collectors.toList());
        // 设备参数集
        List<TblItemParameter> itemParameterList = itemAPI.searchParameterByItemCodes(itemCodeList).getData();
        // 设备信息集
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
            // 匹配参数
            itemParameterList.forEach(parameter->{
                if("OnlineStatus".equals(parameter.getParameterType())){
                    envMonitorItemResultVO.setOnlineParameterCode(parameter.getCode());
                    if(null != parameter.getValue()){
                        envMonitorItemResultVO.setOnlineParameterValue(Optional.ofNullable(parameter.getValue()).orElse("")+Optional.ofNullable(parameter.getUnit()).orElse(""));
                    }
                }
                if("Fault".equals(parameter.getParameterType())){
                    envMonitorItemResultVO.setFaultParameterCode(parameter.getCode());
                    if(null != parameter.getValue()){
                        envMonitorItemResultVO.setFaultParameterValue(Optional.ofNullable(parameter.getValue()).orElse("")+Optional.ofNullable(parameter.getUnit()).orElse(""));
                    }
                }
                if("Alarm".equals(parameter.getParameterType())){
                    envMonitorItemResultVO.setAlarmParameterCode(parameter.getCode());
                    if(null != parameter.getValue()){
                        envMonitorItemResultVO.setAlarmParameterValue(Optional.ofNullable(parameter.getValue()).orElse("")+Optional.ofNullable(parameter.getUnit()).orElse(""));
                    }
                }
                if(EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(alarmRecord.getItemTypeCode()).parameterType.equals(parameter.getParameterType())){
                    if(null != parameter.getValue()){
                        envMonitorItemResultVO.setDataCollectionValue(Optional.ofNullable(parameter.getValue()).orElse("")+Optional.ofNullable(parameter.getUnit()).orElse(""));
                    }
                    envMonitorItemResultVO.setDataCollectionTime(parameter.getDataUpdateTime() == null?parameter.getCreatedTime():parameter.getDataUpdateTime());
                }
            });
            resultVOList.add(envMonitorItemResultVO);
        }
        pageInfoVO.setList(resultVOList);
        return pageInfoVO;
    }

    /**
     * @Author: liwencai
     * @Description: 查询所有的设备详情（分页）
     * @Date: 2022/11/24
     * @Param paramVO:
     * @return: com.thtf.environment.dto.PageInfoVO
     */
    public PageInfoVO listAllItem(EnvMonitorItemParamVO paramVO){
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
            // 匹配参数
            itemParameterList.forEach(parameter->{
                if(parameter.getItemCode().equals(item.getCode())){
                    if("OnlineStatus".equals(parameter.getParameterType())){
                        envMonitorItemResultVO.setOnlineParameterCode(parameter.getCode());
                        if(null != parameter.getValue()){
                            envMonitorItemResultVO.setOnlineParameterValue(Optional.ofNullable(parameter.getValue()).orElse("")+Optional.ofNullable(parameter.getUnit()).orElse(""));
                        }
                    }
                    if("Fault".equals(parameter.getParameterType())){
                        envMonitorItemResultVO.setFaultParameterCode(parameter.getCode());
                        if(null != parameter.getValue()){
                            envMonitorItemResultVO.setFaultParameterValue(Optional.ofNullable(parameter.getValue()).orElse("")+Optional.ofNullable(parameter.getUnit()).orElse(""));
                        }
                    }
                    if("Alarm".equals(parameter.getParameterType())){
                        envMonitorItemResultVO.setAlarmParameterCode(parameter.getCode());
                        if(null != parameter.getValue()){
                            envMonitorItemResultVO.setAlarmParameterValue(Optional.ofNullable(parameter.getValue()).orElse("")+Optional.ofNullable(parameter.getUnit()).orElse(""));
                        }
                    }
                    if(EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(item.getTypeCode()).parameterType.equals(parameter.getParameterType())){
                        if(null != parameter.getValue()){
                                envMonitorItemResultVO.setDataCollectionValue(Optional.ofNullable(parameter.getValue()).orElse("")+Optional.ofNullable(parameter.getUnit()).orElse(""));
                        }
                        envMonitorItemResultVO.setDataCollectionTime(parameter.getDataUpdateTime() == null?parameter.getCreatedTime():parameter.getDataUpdateTime());
                    }
                }
            });
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

    public Map<String, Object> listParameter(List<TblItemParameter> parameterList,Set<String> parameterCodes){
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> innerMap = new HashMap<>();
        for (TblItemParameter parameter : parameterList) {
            parameterCodes.forEach(e->{
                if(parameter.getCode().equals(e)){
                    innerMap.put("value",parameter.getValue());
                    innerMap.put("unit",parameter.getUnit());
                    innerMap.put("valueIsAalarm",this.parameterValueIsTransfinite(parameter.getValue(),parameter.getMax(),parameter.getMin()));
                    resultMap.put(parameter.getCode(),innerMap);
                }
            });
        }
        return resultMap;
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
        List<TimeValueDTO> hourlyHistoryMoment = null;
        if(StringUtils.isBlank(parameterCode)){
            if(StringUtils.isNotBlank(itemTypeCode)){
                System.out.println(itemTypeCode);
                String parameterType = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(itemTypeCode).getParameterType();
                parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
            }else if(StringUtils.isNotBlank(itemCode)){
                TblItem tblItem = new TblItem();
                tblItem.setCode(itemCode);
                List<TblItem> data = itemAPI.queryAllItems(tblItem).getData();
                if(null != data && data.size()>0){
                    String parameterType = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(data.get(0).getTypeCode()).getParameterType();
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
        List<TimeValueDTO> hourlyHistoryMoment = null;
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.addTableShardingValue(TBL_HISTORY_MOMENT,date+DAY_END_SUFFIX);
            if(StringUtils.isBlank(parameterCode)){
                if(StringUtils.isNotBlank(itemTypeCode)){
                    System.out.println(itemTypeCode);
                    String parameterType = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(itemTypeCode).getParameterType();
                    parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
                }else if(StringUtils.isNotBlank(itemCode)){
                    TblItem tblItem = new TblItem();
                    tblItem.setCode(itemCode);
                    List<TblItem> data = itemAPI.queryAllItems(tblItem).getData();
                    if(null != data && data.size()>0){
                        String parameterType = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(data.get(0).getTypeCode()).getParameterType();
                        parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
                    }
                }
//               parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(itemTypeCode).getParameterType(),itemCode).getData();
            }
            try {
                hourlyHistoryMoment = tblHistoryMomentMapper.getDailyHistoryMoment(parameterCode, getMonthStartAndEndTimeDayString(newDate).get("startTime"), getMonthStartAndEndTimeDayString(newDate).get("endTime"));
            }catch (Exception e){
                // hourlyHistoryMoment = null;
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
        List<TimeValueDTO> hourlyHistoryMoment = null;
        try (HintManager hintManager = HintManager.getInstance()) {
            // todo liwencai 此处存在bug
            // 日期是当年的开始时间，至本月时间
            hintManager.addTableShardingValue(TBL_HISTORY_MOMENT,date+DAY_START_SUFFIX);
            if(StringUtils.isBlank(parameterCode)){
                if(StringUtils.isNotBlank(itemTypeCode)){
                    System.out.println(itemTypeCode);
                    String parameterType = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(itemTypeCode).getParameterType();
                    parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
                }else if(StringUtils.isNotBlank(itemCode)){
                    TblItem tblItem = new TblItem();
                    tblItem.setCode(itemCode);
                    List<TblItem> data = itemAPI.queryAllItems(tblItem).getData();
                    if(null != data && data.size()>0){
                        String parameterType = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(data.get(0).getTypeCode()).getParameterType();
                        parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(parameterType,itemCode).getData();
                    }
                }
//                parameterCode = itemAPI.getParameterCodeByTypeAndItemCode(EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(itemTypeCode).getParameterType(),itemCode).getData();
            }
            try {
                // todo liwencai 获取有问题
                hourlyHistoryMoment = tblHistoryMomentMapper.getMonthlyHistoryMoment(parameterCode, getYearStartAndEndTimeMonthString(newDate).get("startTime"),getYearStartAndEndTimeMonthString(newDate).get("endTime"));
            }catch (Exception e){
                // hourlyHistoryMoment = null;
            }
        }
        if(null != hourlyHistoryMoment){
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
        }else {
            hourlyHistoryMoment = new ArrayList<>();
            int year = getYear(newDate);
            for (int i = 1; i <= 12; i++) {
                    TimeValueDTO timeValueDTO = new TimeValueDTO();
                    timeValueDTO.setTime(year+"-"+String.format("%02d", i));
                    timeValueDTO.setValue(0);
                    hourlyHistoryMoment.add(timeValueDTO);
            }
            hourlyHistoryMoment.sort(Comparator.comparing(TimeValueDTO::getValue));
        }
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
    // @Override
    public PageInfoVO listGroupedItemAlarmInfoOld(String sysCode,String groupName,String areaName,String keyword,Integer pageNumber,Integer pageSize) {
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
        Map<Long, Map<String, Object>> groupAboutItemType = itemTypeInfoOfGroupNew(parameterList, pageInfo.getList(), this.getAllParameterCodeNeed());

        /* 匹配信息 */
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (TblGroup group : pageInfo.getList()) {
            Map<String, Object> map = groupAboutItemType.get(group.getId());
//            EnvMonitorGroupDTO envMonitorGroupDTO = new EnvMonitorGroupDTO();
            map.put("id",group.getId());
            map.put("name",group.getName());

//            envMonitorGroupDTO.setId(group.getId());
//            envMonitorGroupDTO.setName(group.getName());
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
                map.put("areaName",stringBuilder.toString().substring(0,stringBuilder.length()-1));
                // envMonitorGroupDTO.setAreaName(stringBuilder.toString().substring(0,stringBuilder.length()-1)); // 去除‘，’
            }




            /* 匹配不同类别的数据 */
            // envMonitorGroupDTO.setResult(groupAboutItemType.get(group.getId()));
            resultList.add(map);
        }
        List<Map<String, String>> title = new ArrayList<>();
        List<String> allParameterCodeNeed = getAllParameterCodeNeed();
        for (String parameterCode : allParameterCodeNeed) {
            Map<String, String> map1 = new HashMap<>();
            map1.put("value",parameterCode);
            map1.put("name",EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByParameterType(parameterCode).getParameterTypeName());
            title.add(map1);
        }
//
//        map.put("title",title);
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

        List<TblItemType> itemTypeList = itemAPI.getItemTypesBySysId(sysCode).getBody().getData();
        List<String> itemTypeCodeList = itemTypeList.stream().map(TblItemType::getCode).collect(Collectors.toList());
        // 获取每个设备对应分组
        List<ItemTypeGroupListDTO> itemTypeGroupListDTO = itemAPI.listItemTypeNestedGroupKeyInfo(sysCode, itemTypeCodeList).getData();
        Map<String, List<TblGroup>> itemTypeGroupMap = new HashMap<>();
        itemTypeGroupListDTO.forEach(e->{
            itemTypeGroupMap.put(e.getItemTypeCode(),e.getGroupInfo());
        });

        for (TblItemType itemType : itemTypeList) {
            GroupAlarmInfoVO groupAlarmInfoVO = new GroupAlarmInfoVO();
            String property;
            try {
                property = EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByTypeCode(itemType.getCode()).getParameterTypeName();
            }catch (Exception e){
                property = itemType.getName();
            }
            groupAlarmInfoVO.setProperty(property);
            groupAlarmInfoVO.setCode(itemType.getCode());
            List<TblGroup> groupList = itemTypeGroupMap.get(itemType.getCode());
            List<Long> list = new ArrayList<>(alarmGroupIdList);
            list.retainAll(groupList.stream().map(TblGroup::getId).collect(Collectors.toList()));
            groupAlarmInfoVO.setValue(list.size());
            resultList.add(groupAlarmInfoVO);
        }
        return resultList;
    }

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
     * @Description:
     * @Date: 2022/11/23
     * @return: java.util.List<java.lang.String>
     */
    List<String> getAllParameterCodeNeed(){
        EnvMonitorItemLiveParameterEnum[] values = EnvMonitorItemLiveParameterEnum.values();
        List<String> result = new ArrayList<>();
        for (EnvMonitorItemLiveParameterEnum envMonitorItemLiveParameterEnum : values) {
            result.add(envMonitorItemLiveParameterEnum.getParameterType());
        }
        return result;
    }

    // 获取设备类别编码
    public Map<Long,Map<String,Object>> itemTypeInfoOfGroupNew(List<TblItemParameter> parameterList,List<TblGroup> groupList,List<String> parameterCodeList){
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
                int totalValue = 0;
                // 需要统计计算平均值的总设备数
                int totalNum = 0;
                // 值单位
                String unit = "";
                if(null != parameterList && parameterList.size()>0){
                    unit = parameterList.get(0).getUnit();
                    for (TblItemParameter itemParameter : parameterList) {
                        if(itemCodeList.contains(itemParameter.getItemCode()) && parameterCode.equals(itemParameter.getParameterType())){
                            num ++;
                            unit = itemParameter.getUnit();
                            if(null != itemParameter.getValue() && StringUtils.isNumeric(itemParameter.getValue())){
                                totalNum ++;
                                totalValue += Integer.parseInt(itemParameter.getValue());
                            }
                        }
                    }
                }
                innerMap.put("averageValue",percent(totalValue,totalNum,1));
                innerMap.put("itemTotalNum", String.valueOf(num));
                innerMap.put("unit",unit);
                innerMap.put("itemTypeCode",EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByParameterType(parameterCode).getItemTypeCode());
                innerMap.put("itemTypeName",EnvMonitorItemLiveParameterEnum.getMonitorItemLiveEnumByParameterType(parameterCode).getItemTypeName());
                map.put(parameterCode,innerMap);
            }
            result.put(group.getId(),map);
        }
        return result;
    }



    /**
     * @Author: liwencai 
     * @Description:
     * @Date: 2022/11/23
     * @Param parameterList: 
     * @Param groupList: 
     * @Param parameterCodeList: 
     * @return: java.util.Map<java.lang.Long,java.util.List<com.thtf.environment.dto.EnvMonitorItemTypeDTO>> 
     */
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
                            if(null != itemParameter.getValue() && StringUtils.isNumeric(itemParameter.getValue())){
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

}
