package com.thtf.environment.dto;

import com.thtf.common.entity.itemserver.TblItemParameter;
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
    // null 正常 0 报警 1 故障
    private Integer alarmCategory;
    private String alarmParameterCode;
    private String alarmParameterValue;
    private String parameterCode;
    private String parameterValue;

    /**
     * 监测参数单位
     */
    private String unit;
    private List<Integer> eye;
    private List<Integer> center;
    private  List<TblItemParameter> parameterList;
}
