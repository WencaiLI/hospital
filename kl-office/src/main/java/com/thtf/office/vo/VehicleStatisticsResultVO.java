package com.thtf.office.vo;

import lombok.Data;

/**
 * @Auther: liwencai
 * @Date: 2022/7/28 09:52
 * @Description: 统计结果ViewObject
 */
@Data
public class VehicleStatisticsResultVO {
    private String attribute; // 统计属性
    private Long number; // 数量
}
