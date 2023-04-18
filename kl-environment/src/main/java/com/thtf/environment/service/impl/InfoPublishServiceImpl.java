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
import com.thtf.common.entity.itemserver.TblVideoItem;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.environment.config.ItemParameterConfig;
import com.thtf.environment.dto.*;
import com.thtf.environment.dto.PageInfoVO;
import com.thtf.environment.dto.convert.AlarmConvert;
import com.thtf.environment.dto.convert.PageInfoConvert;
import com.thtf.environment.dto.convert.ParameterConverter;
import com.thtf.environment.service.InfoPublishService;
import com.thtf.environment.vo.ListLargeScreenInfoParamVO;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liwencai
 * @Date: 2022/9/21 17:23
 * @Description:
 */
@Service
@Slf4j
public class InfoPublishServiceImpl implements InfoPublishService {

    @Resource
    private ItemAPI itemAPI;
    @Resource
    private AlarmAPI alarmAPI;
    @Resource
    private AdminAPI adminAPI;
    @Resource
    private PageInfoConvert pageInfoConvert;
    @Resource
    private ParameterConverter parameterConverter;
    @Resource
    private AlarmConvert alarmConvert;
    @Resource
    private RedisOperationService redisOperationService;
    @Resource
    private ItemParameterConfig itemParameterConfig;
    @Resource
    private CommonService commonService;

    /**
     * @Author: liwencai
     * @Description: 获取大屏信息
     * @Date: 2022/9/23
     * @Param sysCode: 子系统编码
     * @Param areaCode: 区域编码
     * @Param keyword: 关键词
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: java.util.List<com.thtf.environment.dto.ItemInfoOfLargeScreenDTO>
     */
    @Override
    public PageInfo<ItemInfoOfLargeScreenDTO> getLargeScreenInfo(ListLargeScreenInfoParamVO paramVO) {
        PageInfo<ItemInfoOfLargeScreenDTO> pageInfoVO = new PageInfo<>();

        ListItemNestedParametersPageParamDTO listItemNestedParametersPageParam = new ListItemNestedParametersPageParamDTO();
        listItemNestedParametersPageParam.setSysCode(paramVO.getSysCode());
        listItemNestedParametersPageParam.setBuildingCodeList(paramVO.getBuildingCodeList());
        listItemNestedParametersPageParam.setAreaCodeList(paramVO.getAreaCodeList());
        listItemNestedParametersPageParam.setPageNumber(paramVO.getPageNumber());
        listItemNestedParametersPageParam.setPageSize(paramVO.getPageSize());
        if(StringUtils.isNotBlank(paramVO.getKeyword())){
            listItemNestedParametersPageParam.setKeyword(paramVO.getKeyword());
            listItemNestedParametersPageParam.setCodeKey(paramVO.getKeyword());
            listItemNestedParametersPageParam.setAreaKey(paramVO.getKeyword());
            listItemNestedParametersPageParam.setNameKey(paramVO.getKeyword());
        }
        // 运行状态筛选
        if (null != paramVO.getOnlineValue() && "1".equals(paramVO.getOnlineValue())) {

            String parameterValue = commonService.getParameterValueByStateExplain(paramVO.getSysCode(), itemParameterConfig.getInfoPublishOnline(), null, new String[]{"在线", "运行", "行", "上"});
            List<ParameterTypeCodeAndValueDTO> parameterList = new ArrayList<>();
            ParameterTypeCodeAndValueDTO paramTypeCodeAndValueDTO = new ParameterTypeCodeAndValueDTO();
            paramTypeCodeAndValueDTO.setParameterTypeCode(itemParameterConfig.getInfoPublishOnline());
            paramTypeCodeAndValueDTO.setParameterValue(parameterValue);
            parameterList.add(paramTypeCodeAndValueDTO);
            listItemNestedParametersPageParam.setParameterList(parameterList);
        }

        PageInfo<ItemNestedParameterVO> itemPageInfo = itemAPI.listItemNestedParametersPage(listItemNestedParametersPageParam).getData();
        BeanUtils.copyProperties(itemPageInfo,pageInfoVO);
        if(CollectionUtils.isEmpty(itemPageInfo.getList())){
            return pageInfoVO;
        }

        List<ItemNestedParameterVO> list = itemPageInfo.getList();

        // 获取所有设备报警信息
        List<ItemInfoOfLargeScreenDTO> resultList = new ArrayList<>();
        for (ItemNestedParameterVO itemNestedParameterVO : list) {
            ItemInfoOfLargeScreenDTO innerResult = new ItemInfoOfLargeScreenDTO();
            innerResult.setItemId(itemNestedParameterVO.getId());
            innerResult.setItemCode(itemNestedParameterVO.getCode());
            innerResult.setItemName(itemNestedParameterVO.getName());
            innerResult.setAreaCode(itemNestedParameterVO.getAreaCode());
            innerResult.setAreaName(itemNestedParameterVO.getAreaName());
            innerResult.setBuildingCode(itemNestedParameterVO.getBuildingCode());
            innerResult.setAlarmStatus(itemNestedParameterVO.getFault());
            // 匹配模型视角
            if (StringUtils.isNotBlank(itemNestedParameterVO.getViewLongitude())) {
                innerResult.setEye(Arrays.stream(itemNestedParameterVO.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            if (StringUtils.isNotBlank(itemNestedParameterVO.getViewLatitude())) {
                innerResult.setCenter(Arrays.stream(itemNestedParameterVO.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            innerResult.setParameterList(parameterConverter.toParameterInfoList(itemNestedParameterVO.getParameterList()));
            resultList.add(innerResult);
        }
        pageInfoVO.setList(resultList);
        return pageInfoVO;
    }

    /**
     * @Author: liwencai
     * @Description: 获取信息发布大屏报警信息
     * @Date: 2022/9/23
     * @Param sysCode: 子系统编码
     * @Param keyword: 关键词
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: java.util.List<com.thtf.environment.dto.AlarmInfoOfLargeScreenDTO>
     */
    @Override
    public PageInfoVO getLargeScreenAlarmInfo(String sysCode, String buildingCodes, String areaCode, String keyword, Integer pageNumber, Integer pageSize) {

        List<String> buildingCodeList = StringUtils.isNotBlank(buildingCodes) ? Arrays.asList(buildingCodes.split(",")) : adminAPI.listBuildingCodeUserSelf().getData();
        List<String> areaCodeList = StringUtils.isNotBlank(areaCode) ? Arrays.asList(areaCode.split(",")) : null;

        TblItem tblItem = new TblItem();
        tblItem.setSystemCode(sysCode);
        tblItem.setBuildingCodeList(buildingCodeList);
        tblItem.setAreaCodeList(areaCodeList);
        tblItem.setPageNumber(pageNumber);
        tblItem.setPageSize(pageSize);
        tblItem.setFault(ItemConstants.ITEM_FAULT_TRUE);
        tblItem.setAlarm(ItemConstants.ITEM_ALARM_FALSE);
        if(StringUtils.isNotBlank(keyword)){
            tblItem.setKeyName(keyword);
            tblItem.setKeyCode(keyword);
            tblItem.setKeyAreaName(keyword);
        }
        PageInfo<TblItem> pageInfo = itemAPI.queryAllItemsPage(tblItem).getData();



        PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(pageInfo);
        if(CollectionUtils.isEmpty(pageInfoVO.getList())){
            return pageInfoVO;
        }
        List<String> itemCodeList = pageInfo.getList().stream().map(TblItem::getCode).collect(Collectors.toList());
;
        List<TblAlarmRecordUnhandle> alarmList = alarmAPI.getAlarmInfoByItemCodeListAndCategoryLimitOne(itemCodeList, AlarmConstants.FAULT_CATEGORY_INTEGER).getData();

        List<AlarmInfoOfLargeScreenDTO> resultList = new ArrayList<>(pageInfoVO.getList().size());
        pageInfo.getList().forEach(item->{
            AlarmInfoOfLargeScreenDTO innerResult;
            List<TblAlarmRecordUnhandle> collect = alarmList.stream().filter(e -> e.getItemCode().equals(item.getCode())).limit(1).collect(Collectors.toList());
            if(collect.size() == 1){
                innerResult = alarmConvert.toAlarmInfoOfLargeScreenDTO(collect.get(0));
            }else{
                innerResult = new AlarmInfoOfLargeScreenDTO();
            }
            innerResult.setStayTime(commonService.getAlarmStayTime(innerResult.getAlarmTime()));
            // todo 对接高博医院自身的信息发布系统后再写 largeScreen.setPublishContent();
            if (null != item.getViewLongitude()) {
                innerResult.setEye(Arrays.stream(item.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            if (null != item.getViewLatitude()) {
                innerResult.setCenter(Arrays.stream(item.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
            resultList.add(innerResult);

        });
        pageInfoVO.setList(resultList);
        return pageInfoVO;
    }

    /**
     * @Author: liwencai
     * @Description: 远程开关
     * @Date: 2022/11/1
     * @Param: sysCode: 子系统编码
     * @Param: itemCodeList: 设备编码集
     * @Return: java.lang.Boolean
     */
    @Override
    @Transactional
    // todo liwencai 和前端了解控制原理并修改接口
    public Boolean remoteSwitch(String sysCode, String itemCodes) {
//        if( itemAPI.negateBooleanParameter(itemCodes,itemParameterConfig.getState()).getData()){
//            redisOperationService.remoteSwitchItemStatusByItemCodeList(Arrays.stream(itemCodes.split(",")).collect(Collectors.toList()));
//        }
        return true;
    }

    /**
     * @Author: liwencai
     * @Description: 新增播单
     * @Date: 2022/11/1
     * @Param: param:
     * @Return: java.lang.Boolean
     */
    @Override
    public Boolean insertPlayOrder(ItemPlayInfoDTO param) {
        redisOperationService.insertPlayOrder(param);
        return true;
    }

    /**
     * @Author: liwencai
     * @Description: 获取信息发布大屏内容
     * @Date: 2022/11/2
     * @Param: sysCode: 子系统编码
     * @Param: buildingCodes: 建筑编码集
     * @Param: areaCode: 区域编码
     * @Param: itemCodes: 设备编码集
     * @Return: com.thtf.common.response.JsonResult<java.util.List < com.thtf.environment.dto.ItemPlayInfoDTO>>
     */
    @Override
    public List<ItemPlayInfoDTO> listLargeScreenContent(String sysCode, String buildingCodes, String areaCode, String itemCodes) {
        List<String> buildingCodeList = StringUtils.isNotBlank(buildingCodes) ? Arrays.asList(buildingCodes.split(",")) : adminAPI.listBuildingCodeUserSelf().getData();
        List<String> areaCodesList = StringUtils.isNotBlank(areaCode) ? Arrays.asList(areaCode.split(",")) : null;
        List<String> itemCodeList = StringUtils.isNotBlank(itemCodes) ? Arrays.asList(itemCodes.split(",")) : null;

        // 根据区域和子系统获取和建筑编码获取大屏系统
        TblItem tblItem = new TblItem();
        tblItem.setSystemCode(sysCode);
        tblItem.setBuildingCodeList(buildingCodeList);
        tblItem.setAreaCodeList(areaCodesList);
        tblItem.setCodeList(itemCodeList);
        List<TblItem> itemList = itemAPI.queryAllItems(tblItem).getData();

        if(CollectionUtils.isEmpty(itemList)){
            return Collections.emptyList();
        }

        List<ItemPlayInfoDTO> resultList = new ArrayList<>();
        // 获取大屏id
        itemCodeList = itemList.stream().map(TblItem::getCode).collect(Collectors.toList());
        for (String itemCode : itemCodeList) {
            ItemPlayInfoDTO result;
            // 从redis里获取缓存的
            try {
                List<ItemPlayInfoDTO> playOrderByItemCode = redisOperationService.getPlayOrderByItemCode(itemCode);
                if (playOrderByItemCode != null && playOrderByItemCode.size() > 0) {
                    result = playOrderByItemCode.get(0);
                    List<TblVideoItem> data = itemAPI.getVideoItemListByItemCode(itemCode).getBody().getData();
                    if (null != data && data.size() > 0) { // todo 获取方式存在问题
                        result.setVideoItemInfo(data.get(0));
                    }
                    resultList.add(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/30
     * @Param sysCode: 子系统编码
     * @Param itemCode: 设备编码
     * @return: com.thtf.environment.dto.InfoPublishPointDTO
     */
    @Override
    public ItemInfoOfLargeScreenDTO getMonitorPoint(String sysCode, String itemCode) {
        ListItemNestedParametersParamDTO listItemParam = new ListItemNestedParametersParamDTO();
        listItemParam.setSysCode(sysCode);
        listItemParam.setItemCodeList(Collections.singletonList(itemCode));
        List<ListItemNestedParametersResultDTO> itemNestedParameterVOList = itemAPI.listItemNestedParameters(listItemParam).getData();
        if (CollectionUtils.isEmpty(itemNestedParameterVOList)) {
            return null;
        }
        ListItemNestedParametersResultDTO itemNestedParameterVO = itemNestedParameterVOList.get(0);
        ItemInfoOfLargeScreenDTO innerResult = new ItemInfoOfLargeScreenDTO();
        innerResult.setAreaCode(itemNestedParameterVO.getBuildingAreaCode());
        innerResult.setAreaName(itemNestedParameterVO.getBuildingAreaName());
        innerResult.setAlarmStatus(itemNestedParameterVO.getFault());
        BeanUtils.copyProperties(itemNestedParameterVO,innerResult);
        innerResult.setParameterList(parameterConverter.toParameterInfoList(itemNestedParameterVO.getParameterList()));
        return innerResult;
    }

    /**
     * @Author: liwencai
     * @Description: 前端展示数据
     * @Date: 2023/1/30
     * @Param sysCode: 子系统编码
     * @Param buildingCodes: 建筑编码集
     * @Param areaCode: 区域编码
     * @Param itemTypeCodes:
     * @Return: com.thtf.environment.dto.InfoPublishDisplayDTO
     */
    @Override
    public InfoPublishDisplayDTO getDisplayInfo(String sysCode, String buildingCodes, String areaCode, String itemTypeCodes) {
        InfoPublishDisplayDTO result = new InfoPublishDisplayDTO();
        List<String> buildingCodeList = StringUtils.isNotBlank(buildingCodes) ? Arrays.asList(buildingCodes.split(",")) : adminAPI.listBuildingCodeUserSelf().getData();
        List<String> areaCodeList = StringUtils.isNotBlank(areaCode) ? Arrays.asList(areaCode.split(",")) : null;
        List<String> itemTypeCodeList = StringUtils.isNotBlank(itemTypeCodes) ? Arrays.asList(itemTypeCodes.split(",")) : null;

        // 设备总数 报警设备数 故障设备总数
        CountItemInfoParamDTO countItemInfoParam = new CountItemInfoParamDTO();
        countItemInfoParam.setSysCode(sysCode);
        countItemInfoParam.setBuildingCodeList(buildingCodeList);
        countItemInfoParam.setAreaCodeList(areaCodeList);
        countItemInfoParam.setItemTypeCodeList(itemTypeCodeList);
        CountItemInfoResultDTO itemInfo = itemAPI.countItemInfo(countItemInfoParam).getData();
        result.setTotalCount(itemInfo.getItemNumber());
        result.setAlarmNumber(itemInfo.getAlarmItemNumber());
        result.setFaultNumber(itemInfo.getFaultItemNumber());

        CountItemByParameterListDTO countItemByParameterListDTO = new CountItemByParameterListDTO();
        countItemByParameterListDTO.setSysCode(sysCode);
        countItemByParameterListDTO.setBuildingCodeList(buildingCodeList);
        if (null == buildingCodeList || buildingCodeList.size() == 0) {
            countItemByParameterListDTO.setAreaCode(areaCode);
        }
        countItemByParameterListDTO.setItemTypeCodeList(itemTypeCodeList);

        // 查看在线数量
        countItemByParameterListDTO.setParameterTypeCode(itemParameterConfig.getInfoPublishOnline());
        String parameterValue = commonService.getParameterValueByStateExplain(sysCode, itemParameterConfig.getInfoPublishOnline(), itemTypeCodes, new String[]{"在线", "上"});
        countItemByParameterListDTO.setParameterValue(parameterValue);
        Integer onlineCount = itemAPI.countItemByParameterList(countItemByParameterListDTO).getData();
        result.setOnlineCount(onlineCount);

        // 查看开启数量
        countItemByParameterListDTO.setParameterTypeCode(itemParameterConfig.getState());
        countItemByParameterListDTO.setParameterValue(null);
        String parameterValueState = commonService.getParameterValueByStateExplain(sysCode, itemParameterConfig.getInfoPublishOnline(), itemTypeCodes, new String[]{"运行", "运", "行"});
        countItemByParameterListDTO.setParameterValue(parameterValueState);
        Integer onCount = itemAPI.countItemByParameterList(countItemByParameterListDTO).getData();
        result.setOnCount(onCount);
        return result;
    }

    /* *************************** 复用代码区域 开始 ************************** */

    /* *************************** 复用代码区域 结束 ************************** */
}
