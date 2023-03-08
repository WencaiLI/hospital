package com.thtf.elevator.vo;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * @Author: liwencai
 * @Date: 2023/3/8 10:35
 * @Description:
 */
@Data
@Validated
public class QueryItemParamVO {
    @NotBlank
    private String sysCode;
    private String buildingCodes;
    private String areaCode;
    private String itemTypeCodes;
}
