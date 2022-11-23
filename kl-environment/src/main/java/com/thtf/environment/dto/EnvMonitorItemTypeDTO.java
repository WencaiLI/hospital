package com.thtf.environment.dto;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/10/31 16:51
 * @Description:
 */
@Data
public class EnvMonitorItemTypeDTO {
    /**
     * 设备类别编码
     */
    private String itemTypeCode;

    /**
     * 设备类别名称
     */
    private String itemTypeName;

    /**
     * 设备总数
     */
    private Integer itemTotalNum;

    /**
     * 平均值
     */
    private String averageValue;

    /**
     * 单位
     */
    private String unit;

    /**
     *  是否报警
     */
    private Boolean isAlarm;
}
