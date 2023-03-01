package com.thtf.elevator.service.impl;

import com.thtf.common.dto.alarmserver.CountAlarmParamDTO;
import com.thtf.common.dto.alarmserver.CountAlarmResultDTO;
import com.thtf.common.dto.itemserver.ItemTotalAndOnlineAndAlarmNumDTO;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.common.entity.itemserver.TblItemType;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.elevator.common.constant.ItemTypeConstant;
import com.thtf.elevator.common.constant.ParameterConstant;
import com.thtf.elevator.dto.DisplayInfoDTO;
import com.thtf.elevator.dto.KeyValueDTO;
import com.thtf.elevator.dto.convert.FloorConverter;
import com.thtf.elevator.dto.convert.ItemConverter;
import com.thtf.elevator.dto.convert.PageInfoConvert;
import com.thtf.elevator.dto.convert.ParameterConverter;
import com.thtf.elevator.service.ElevatorAppService;
import com.thtf.elevator.vo.AppAlarmInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<DisplayInfoDTO> displayInfo(String sysCode, String buildingCodes, String areaCode) {
        List<DisplayInfoDTO> result = new ArrayList<>();
        // 获取电梯的所有子类,这里假设只有一级父级
        TblItemType tblItemType = new TblItemType();
        tblItemType.setSysCode(sysCode);
        tblItemType.setIsLeaf(ItemTypeConstant.IS_LEAF);
        // 父类为itemType的设备类别
        List<TblItemType> itemTypeList = itemAPI.queryAllItemTypes(tblItemType).getData();
        // 根据类别查询所有的信息
        for (TblItemType itemType : itemTypeList) {

            ItemTotalAndOnlineAndAlarmNumDTO itemTypeInfo = itemAPI.getItemOnlineAndTotalAndAlarmItemNumber(sysCode, areaCode, buildingCodes, itemType.getCode(),
                    ParameterConstant.ELEVATOR_RUN_STATUS, ParameterConstant.ELEVATOR_RUN_TRUE, false, true).getData();

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
     * @Description:
     * @Date: 2023/2/28
     * @Param sysCode:
     * @Param buildingCodes:
     * @Param areaCode:
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
}
