package com.thtf.environment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.alarmserver.ItemAlarmInfoDTO;
import com.thtf.common.dto.itemserver.ItemTotalAndOnlineAndAlarmNumDTO;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.environment.dto.PageInfoVO;
import com.thtf.environment.dto.convert.ItemTypeConvert;
import com.thtf.environment.dto.convert.PageInfoConvert;
import com.thtf.environment.entity.TblHistoryMoment;
import com.thtf.environment.mapper.TblHistoryMomentMapper;
import com.thtf.environment.service.EnvMonitorService;
import com.thtf.environment.vo.CodeNameVO;
import com.thtf.environment.vo.EChartsVO;
import com.thtf.environment.vo.EnvMonitorItemParamVO;
import com.thtf.environment.vo.EnvMonitorItemResultVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    ItemTypeConvert itemTypeConvert;

    @Resource
    PageInfoConvert pageInfoConvert;

    private final static String TBL_HISTORY_MOMENT = "tbl_history_moment";

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
        if(null != paramVO.getAlarmCategory()){
            PageInfo<TblItem> pageInfo = itemAPI.searchItemBySysCodeAndTypeCodeAndAreaCodeListAndKeywordPage(paramVO.getSysCode(), paramVO.getItemTypeCode(), paramVO.getKeyword(), null, paramVO.getPageNumber(), paramVO.getPageSize()).getData();
            itemCodeList = pageInfo.getList().stream().map(TblItem::getCode).collect(Collectors.toList());
            List<TblItemParameter> itemParameterList = itemAPI.searchParameterByItemCodes(itemCodeList).getData();
            PageInfoVO pageInfoVO = pageInfoConvert.toPageInfoVO(pageInfo);
            for (TblItem item : pageInfo.getList()) {
                EnvMonitorItemResultVO envMonitorItemResultVO = new EnvMonitorItemResultVO();
                envMonitorItemResultVO.setItemCode(item.getCode());
                envMonitorItemResultVO.setItemName(item.getName());
                envMonitorItemResultVO.setAreaCode(item.getAreaCode());
                envMonitorItemResultVO.setAreaName(item.getAreaName());
                envMonitorItemResultVO.setEye(Arrays.stream(item.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                envMonitorItemResultVO.setCenter(Arrays.stream(item.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));

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
            for (TblAlarmRecordUnhandle alarmRecord: pageInfo.getList()) {
                EnvMonitorItemResultVO envMonitorItemResultVO = new EnvMonitorItemResultVO();
                // 匹配设备信息
                itemList.forEach(item->{
                    if(item.getCode().equals(alarmRecord.getItemCode())){
                        envMonitorItemResultVO.setItemCode(item.getCode());
                        envMonitorItemResultVO.setItemName(item.getName());
                        envMonitorItemResultVO.setAreaCode(item.getAreaCode());
                        envMonitorItemResultVO.setAreaName(item.getAreaName());
                        envMonitorItemResultVO.setEye(Arrays.stream(item.getViewLongitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                        envMonitorItemResultVO.setCenter(Arrays.stream(item.getViewLatitude().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
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

    /* =============================== 复用代码区 ==================================== */

    private Map<String,String> getTodayStartTimeAndEndTimeString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String,String> result = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        result.put("startTime",LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),0,0,0).format(dtf));
        result.put("endTime",LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),23,59,59).format(dtf));
        return result;
    }
}
