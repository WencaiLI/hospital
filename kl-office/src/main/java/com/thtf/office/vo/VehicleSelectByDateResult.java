package com.thtf.office.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/7/28 17:45
 * @Description: 根据日月公车信息查询结果集
 */
@Data
public class VehicleSelectByDateResult {
    private Long id; // id
    private String attribute; // 查询项
    private Long dayNumber; // 每日数量
    private Long monthNumber; // 每月数量
}
