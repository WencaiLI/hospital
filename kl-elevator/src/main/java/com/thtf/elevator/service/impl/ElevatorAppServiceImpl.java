package com.thtf.elevator.service.impl;

import com.github.pagehelper.PageInfo;
import com.thtf.common.constant.AlarmConstants;
import com.thtf.common.constant.ItemConstants;
import com.thtf.common.constant.ItemTypeConstants;
import com.thtf.common.dto.alarmserver.CountAlarmParamDTO;
import com.thtf.common.dto.alarmserver.CountAlarmResultDTO;
import com.thtf.common.dto.itemserver.ItemNestedParameterVO;
import com.thtf.common.dto.itemserver.ItemTotalAndOnlineAndAlarmNumDTO;
import com.thtf.common.dto.itemserver.ListItemNestedParametersPageParamDTO;
import com.thtf.common.dto.itemserver.ParameterTypeCodeAndValueDTO;
import com.thtf.common.entity.itemserver.TblItemType;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.elevator.config.ItemParameterConfig;
import com.thtf.elevator.dto.DisplayInfoDTO;
import com.thtf.elevator.dto.KeyValueDTO;
import com.thtf.elevator.service.ElevatorAppService;
import com.thtf.elevator.vo.AppAlarmInfoVO;
import com.thtf.elevator.vo.AppItemSortDTO;
import com.thtf.elevator.vo.AppItemSortVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2023/2/28 18:48
 * @Description:
 */
@Service
public class ElevatorAppServiceImpl implements ElevatorAppService {

    @Resource
    private AlarmAPI alarmAPI;

    @Resource
    private ItemAPI itemAPI;

    @Resource
    private ItemParameterConfig itemParameterConfig;

    @Resource
    private CommonService commonService;


    @Override
    public List<DisplayInfoDTO> displayInfo(String sysCode, String buildingCodes, String areaCode) {

        // 获取设备参数为运行时的状态值
        String parameterValueByStateExplain = commonService.getParameterValueByStateExplain(sysCode, itemParameterConfig.getState(), null, new String[]{"运","行"});

        List<DisplayInfoDTO> result = new ArrayList<>();
        // 获取电梯的所有子类,这里假设只有一级父级
        TblItemType tblItemType = new TblItemType();
        tblItemType.setSysCode(sysCode);
        tblItemType.setIsLeaf(ItemTypeConstants.IS_LEAF);
        // 父类为itemType的设备类别
        List<TblItemType> itemTypeList = itemAPI.queryAllItemTypes(tblItemType).getData();
        // 根据类别查询所有的信息
        for (TblItemType itemType : itemTypeList) {

            ItemTotalAndOnlineAndAlarmNumDTO itemTypeInfo = itemAPI.getItemOnlineAndTotalAndAlarmItemNumber(sysCode, areaCode, buildingCodes, itemType.getCode(),
                    itemParameterConfig.getState(), parameterValueByStateExplain, false, true).getData();

            // 设备总数
            List<KeyValueDTO> kvList = new ArrayList<>();
            KeyValueDTO keyValueDTO1 = new KeyValueDTO();
            keyValueDTO1.setKey(itemType.getName()+"总数");
            keyValueDTO1.setValue(itemTypeInfo.getTotalNum());
            kvList.add(keyValueDTO1);
            // 运行总数
            KeyValueDTO keyValueDTO2 = new KeyValueDTO();
            keyValueDTO2.setKey("运行总数");
            keyValueDTO2.setValue(0);
            keyValueDTO2.setValue(itemTypeInfo.getOnlineNum());
            kvList.add(keyValueDTO2);
            // 报警总数
            KeyValueDTO keyValueDTO3 = new KeyValueDTO();
            keyValueDTO3.setKey("报警总数");
            keyValueDTO3.setValue(0);
            keyValueDTO3.setValue(itemTypeInfo.getMonitorAlarmNumber());
            kvList.add(keyValueDTO3);
            // 故障总数
            KeyValueDTO keyValueDTO4 = new KeyValueDTO();
            keyValueDTO4.setKey("故障总数");
            keyValueDTO4.setValue(0);
            keyValueDTO4.setValue(itemTypeInfo.getMalfunctionAlarmNumber());
            kvList.add(keyValueDTO4);
            DisplayInfoDTO displayInfoDTO = new DisplayInfoDTO();
            displayInfoDTO.setResults(kvList);
            result.add(displayInfoDTO);
        }
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 获取报警信息
     * @Date: 2023/2/28
     * @Param sysCode: 子系统编码
     * @Param buildingCodes: 建筑编码集
     * @Param areaCode: 区域编码
     * @Return: com.thtf.elevator.vo.AppAlarmInfoVO
     */
    @Override
    public AppAlarmInfoVO getAlarmInfo(String sysCode, String buildingCodes, String areaCode) {
        AppAlarmInfoVO result = new AppAlarmInfoVO();
        List<String> areaCodeList = null;
        List<String> buildingCodeList = null;
        if(StringUtils.isNotBlank(areaCode)){
            areaCodeList = Arrays.asList(areaCode.split(","));
        }else {
            if (StringUtils.isNotBlank(buildingCodes)){
                buildingCodeList = Arrays.asList(buildingCodes.split(","));
            }
        }
        CountAlarmParamDTO countAlarmParamDTO = new CountAlarmParamDTO();
        countAlarmParamDTO.setSysCode(sysCode);
        countAlarmParamDTO.setAreaCodeList(areaCodeList);
        countAlarmParamDTO.setBuildingCodeList(buildingCodeList);
        countAlarmParamDTO.setIsNeedUnHandle(true);
        countAlarmParamDTO.setIsNeedHasHandled(true);
        CountAlarmResultDTO alarmInfo = alarmAPI.countAlarmAll(countAlarmParamDTO).getData();
        if(null != alarmInfo){
            result.setAlarmUnHandleNum(alarmInfo.getUnhandledAlarmNum());
            result.setAlarmHasHandledNum(alarmInfo.getHasHandledAlarmNum());
        }
        return result;
    }

    @Override
    public PageInfo<AppItemSortVO> listItem(AppItemSortDTO param) {
        ListItemNestedParametersPageParamDTO listItemNestedParametersPageParamDTO = new ListItemNestedParametersPageParamDTO();

        BeanUtils.copyProperties(param,listItemNestedParametersPageParamDTO);
        if(StringUtils.isNotBlank(param.getAreaCodes())){
            listItemNestedParametersPageParamDTO.setAreaCodeList(Arrays.asList(param.getAreaCodes().split(",")));
        }else {
            if (StringUtils.isNotBlank(param.getBuildingCodes())){
                listItemNestedParametersPageParamDTO.setBuildingCodeList(Arrays.asList(param.getBuildingCodes().split(",")));
            }
        }
        if (StringUtils.isNotBlank(param.getItemTypeCodes())){
            listItemNestedParametersPageParamDTO.setItemTypeCodeList(Arrays.asList(param.getItemTypeCodes().split(",")));
        }

        List<ParameterTypeCodeAndValueDTO> parameters = new ArrayList<>();
        Integer runStatus = param.getRunStatus();
        if(null != runStatus){
            ParameterTypeCodeAndValueDTO parameter = new ParameterTypeCodeAndValueDTO();
            parameter.setParameterTypeCode(itemParameterConfig.getState());
            parameter.setParameterValue(String.valueOf(param.getRunStatus()));
            parameters.add(parameter);
        }
        listItemNestedParametersPageParamDTO.setParameterList(parameters);

        if(StringUtils.isNotBlank(param.getAlarmCategory())){
            if(AlarmConstants.ALARM_CATEGORY_INTEGER.toString().equals(param.getAlarmCategory())){
                listItemNestedParametersPageParamDTO.setAlarm(ItemConstants.ITEM_ALARM_TRUE);
            }
            if (AlarmConstants.FAULT_CATEGORY_INTEGER.toString().equals(param.getAlarmCategory())){
                listItemNestedParametersPageParamDTO.setAlarm(ItemConstants.ITEM_ALARM_FALSE);
                listItemNestedParametersPageParamDTO.setFault(ItemConstants.ITEM_FAULT_TRUE);
            }
        }
        PageInfo<ItemNestedParameterVO> pageInfo = itemAPI.listItemNestedParametersPage(listItemNestedParametersPageParamDTO).getData();

        // PageInfoVO<AppItemSortVO> pageInfoVO = pageInfoConvert.toPageInfoVO(data);

        PageInfo<AppItemSortVO> pageInfoVO = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo,pageInfoVO);
        if(!CollectionUtils.isEmpty(pageInfo.getList())){
            List<AppItemSortVO> resultList = new ArrayList<>();
            pageInfo.getList().forEach(e->{
                AppItemSortVO appItemSortVO = new AppItemSortVO();
                appItemSortVO.setItemName(e.getName());
                appItemSortVO.setItemCode(e.getCode());
                e.getParameterList().forEach(parameter -> {
                    if (itemParameterConfig.getState().equals(parameter.getParameterType())){
                        appItemSortVO.setRunStatus(parameter.getValue());
                    }
                });
                if (ItemConstants.ITEM_ALARM_TRUE.equals(e.getAlarm())){
                    appItemSortVO.setAlarmCategory(AlarmConstants.ALARM_CATEGORY_INTEGER.toString());
                }
                if (!ItemConstants.ITEM_ALARM_FALSE.equals(e.getAlarm()) && ItemConstants.ITEM_FAULT_TRUE.equals(e.getFault())){
                    appItemSortVO.setAlarmCategory(AlarmConstants.FAULT_CATEGORY_INTEGER.toString());
                }
                resultList.add(appItemSortVO);

            });
            pageInfoVO.setList(resultList);
            return pageInfoVO;
        }else {
            return pageInfoVO;
        }
    }
}
