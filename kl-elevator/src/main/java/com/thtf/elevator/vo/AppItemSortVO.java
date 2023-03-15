package com.thtf.elevator.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2023/3/2 16:37
 * @Description:
 */
@Data
public class AppItemSortVO {
    /**
     * 设备名称
     */
    private String itemName;
    /**
     * 设备编码
     */
    private String itemCode;
    /**
     * 运行状态
     */
    private String runStatus;
    /**
     * 报警类别
     */
    private String alarmCategory;
}
