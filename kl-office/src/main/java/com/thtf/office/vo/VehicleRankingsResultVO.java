package com.thtf.office.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/7/28 14:48
 * @Description: 公车使用排行榜结果ViewObject
 */
@Data
public class VehicleRankingsResultVO {
    private Long top;
    private String attribute;
    private Long number;
}
