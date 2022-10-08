package com.thtf.elevator.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: liwencai
 * @Date: 2022/10/8 09:58
 * @Description:
 */
@Data
public class ParameterInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonSerialize(
            using = ToStringSerializer.class
    )
    private Long id;
    private String name;
    private String code;
    private String parameterType;
    private String infoType;
    private String valueType;
    private String unit;
    private String valuePrecision;
    private String max;
    private String min;
    private String stateExplain;
    private String pointType;
    private String itemCode;
    private String value;
}
