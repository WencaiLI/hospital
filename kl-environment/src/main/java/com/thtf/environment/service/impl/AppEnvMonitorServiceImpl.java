package com.thtf.environment.service.impl;

import com.thtf.common.constant.AlarmConstants;
import com.thtf.common.constant.ItemConstants;
import com.thtf.common.dto.alarmserver.ItemAlarmInfoDTO;
import com.thtf.common.dto.alarmserver.ListAlarmPageParamDTO;
import com.thtf.common.dto.itemserver.ItemGroupParamVO;
import com.thtf.common.dto.itemserver.ParameterTemplateAndDetailDTO;
import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.common.entity.itemserver.TblGroup;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.environment.dto.AppEnvMonitorDisplayDTO;
import com.thtf.environment.dto.AppListAlarmParamDTO;
import com.thtf.environment.dto.KeyValueDTO;
import com.thtf.environment.service.AppEnvMonitorService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liwencai
 * @Date: 2022/12/15 21:10
 * @Description:
 */
@Service
public class AppEnvMonitorServiceImpl implements AppEnvMonitorService {
    @Autowired
    private ItemAPI itemAPI;

    @Autowired
    private AlarmAPI alarmAPI;

    @Autowired
    private EnvMonitorServiceImpl envMonitorServiceImpl;

    /**
     * @Author: liwencai
     * @Description: 前端信息
     * @Date: 2023/3/15
     * @Param sysCode:
     * @Param buildingCodes:
     * @Return: com.thtf.environment.dto.AppEnvMonitorDisplayDTO
     */
    @Override
    public AppEnvMonitorDisplayDTO getDisplayInfo(String sysCode, String buildingCodes) {
        ItemGroupParamVO itemGroupParamVO = new ItemGroupParamVO();
        itemGroupParamVO.setSystemCode(sysCode);
        itemGroupParamVO.setBuildingCodes(buildingCodes);
        AppEnvMonitorDisplayDTO result = new AppEnvMonitorDisplayDTO();
        result.setGroupNum(itemAPI.queryAllGroup(itemGroupParamVO).getData().size());
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 报警数量统计
     * @Date: 2023/3/15
     * @Param sysCode: 子系统编码
     * @Param buildingCodes: 建筑编码集
     * @Return: java.util.List<com.thtf.environment.dto.KeyValueDTO>
     */
    @Override
    public List<KeyValueDTO> getAlarmCount(String sysCode, String buildingCodes){
        List<KeyValueDTO> result = new ArrayList<>();
        List<ParameterTemplateAndDetailDTO> parameterInfo = envMonitorServiceImpl.getParameterInfo();
        List<String> itemTypeList = parameterInfo.stream().map(ParameterTemplateAndDetailDTO::getItemTypeCode).collect(Collectors.toList());
        itemTypeList = CollectionUtils.isNotEmpty(itemTypeList)?itemTypeList:null;
        List<ItemAlarmInfoDTO> data = alarmAPI.getItemTypeAlarmSituation(sysCode, null, buildingCodes, AlarmConstants.ALARM_CATEGORY_INTEGER, itemTypeList, true, null, null).getData();
        for (ParameterTemplateAndDetailDTO parameter : parameterInfo) {
            KeyValueDTO keyValueDTO = new KeyValueDTO();
            keyValueDTO.setKey(parameter.getName().split("[(]")[0].split("（")[0]);
            data.forEach(item->{
                if(parameter.getItemTypeCode().equals(item.getAttribute().toString())){
                    keyValueDTO.setValue(item.getMonitorAlarmNumber()) ;
                }
            });
            result.add(keyValueDTO);
        }
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 未处理报警信息
     * @Date: 2023/3/15
     * @Param paramDTO:
     * @Return: java.lang.Object
     */
    @Override
    public Object listAlarmUnhandled(AppListAlarmParamDTO paramDTO){
        ListAlarmPageParamDTO listAlarmPageParamDTO = new ListAlarmPageParamDTO();
        listAlarmPageParamDTO.setAlarmCategoryList(Collections.singletonList(AlarmConstants.ALARM_CATEGORY_INTEGER));
        listAlarmPageParamDTO.setSysCode(paramDTO.getSysCode());
        listAlarmPageParamDTO.setBuildingCodes(paramDTO.getBuildingCodes());
        listAlarmPageParamDTO.setAreaCodes(paramDTO.getAreaCodes());
        if(null != paramDTO.getStartTime() && null != paramDTO.getEndTime()){
            listAlarmPageParamDTO.setStartTime(paramDTO.getStartTime());
            listAlarmPageParamDTO.setEndTime(paramDTO.getEndTime());
        }
        listAlarmPageParamDTO.setPageNumber(paramDTO.getPageNumber());
        listAlarmPageParamDTO.setPageSize(paramDTO.getPageSize());
        List<String> itemCodeList = new ArrayList<>();
        if(null != paramDTO.getGroupIds() && paramDTO.getGroupIds().size() > 0){
            List<TblGroup> data = itemAPI.searchGroupByIdList(paramDTO.getGroupIds()).getData();
            for (TblGroup group : data) {
                itemCodeList.addAll(group.getItemCodeList());
            }
        }
        listAlarmPageParamDTO.setItemCodeList(itemCodeList);
        if(null != paramDTO.getItemTypeCodeList() && paramDTO.getItemTypeCodeList().size() > 0){
            listAlarmPageParamDTO.setItemTypeCodeList(paramDTO.getItemTypeCodeList());
        }
        return alarmAPI.listAlarmUnhandled(listAlarmPageParamDTO).getData();
    }

    /**
     * @Author: liwencai
     * @Description: 获取组信息
     * @Date: 2023/3/15
     * @Param sysCode:
     * @Param buildingCodes:
     * @Return: java.util.List<com.thtf.environment.dto.KeyValueDTO>
     */
    @Override
    public List<KeyValueDTO> listGroupInfo(String sysCode, String buildingCodes){
        List<KeyValueDTO> resultList = new ArrayList<>();
        ItemGroupParamVO itemGroupParamVO = new ItemGroupParamVO();
        itemGroupParamVO.setSystemCode(sysCode);
        List<TblGroup> data = itemAPI.queryAllGroup(itemGroupParamVO).getData();
        for (TblGroup group : data) {
            KeyValueDTO result = new KeyValueDTO();
            result.setKey(group.getName());
            result.setValue(group.getId());
            resultList.add(result);
        }
        return resultList;
    }

    /**
     * @Author: liwencai
     * @Description: 获取类别信息
     * @Date: 2023/3/15
     * @Param sysCode:
     * @Return: java.util.List<com.thtf.environment.dto.KeyValueDTO>
     */
    @Override
    public List<KeyValueDTO> listTypeInfo(String sysCode){
        List<KeyValueDTO> resultList = new ArrayList<>();
        List<ParameterTemplateAndDetailDTO> parameterInfo = envMonitorServiceImpl.getParameterInfo();
        for (ParameterTemplateAndDetailDTO parameter : parameterInfo) {
            KeyValueDTO result = new KeyValueDTO();
            result.setKey(parameter.getName().split("[(]")[0].split("（")[0]);
            result.setValue(parameter.getItemTypeCode());
            resultList.add(result);
        }
        return resultList;
    }
}
