package com.thtf.environment.dto;

import com.thtf.common.dto.itemserver.ItemNestedParameterVO;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.environment.vo.ItemParameterInfoVO;
import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/12/13 19:12
 * @Description:
 */
@Data
public class EnvItemMonitorDTO {
    private Long itemId;
    private String itemCode;
    private String itemName;
    private String itemTypeCode;
    private String itemTypeName;
    private String buildingAreaCode;
    private String buildingAreaName;
    private String buildingCode;
    private String description;
    private String systemCode;
    private String systemName;
    private String alarmParameterCode;
    private String alarmParameterValue;
//    private String faultParameterCode;
//    private String faultParameterValue;
    private String onlineParameterCode;
    private String onlineParameterValue;
//    private String stateParameterCode;
//    private String stateParameterValue;
    private List<Integer> eye;
    private List<Integer> center;
    private  List<TblItemParameter> parameterList;
}
