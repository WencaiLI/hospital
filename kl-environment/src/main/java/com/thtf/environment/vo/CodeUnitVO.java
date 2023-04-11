package com.thtf.environment.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liwencai
 * @sine 2023/4/11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeUnitVO {
    private String code;
    private String unit;
}
