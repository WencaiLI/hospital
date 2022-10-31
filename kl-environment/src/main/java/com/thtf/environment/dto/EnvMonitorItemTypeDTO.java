package com.thtf.environment.dto;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/10/31 16:51
 * @Description:
 */
@Data
public class EnvMonitorItemTypeDTO {
    private String itemTypeCode; // 设备类别编码
    private String itemTypeName; // 设备类别名称
    private Integer itemTotalNum; // 设备总数
    private String averageValue; // 平均值
    private String unit; // 单位
    private Boolean isAlarm; // 是否报警
}
