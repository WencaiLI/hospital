package com.thtf.elevator.vo;

import com.thtf.common.dto.itemserver.ParameterTypeCodeAndValueDTO;
import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2023/3/2 15:58
 * @Description:
 */
@Data
public class AppItemSortDTO {
    private String sysCode;
    private String buildingCodes;
    private String areaCodes;
    private String itemTypeCodes;
    private List<String> parameterTypeCodeList;
    private String alarmCategory;
    // 0 关闭 1 开启
    private Integer runStatus;
    private List<ParameterTypeCodeAndValueDTO> parameterList;
    private List<String> itemCodeList;
    private String keyword;
    private String codeKey;
    private String nameKey;
    private String areaKey;
    private String descKey;
    private Integer pageNumber;
    private Integer pageSize;
}
