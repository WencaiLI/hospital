package com.thtf.environment.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/25 14:39
 * @Description:
 */
@Data
public class EChartsVO {
    private List<String> keys;
    private List<Object> values;
}
