package com.thtf.environment.dto;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/9/22 21:27
 * @Description: 大屏信息
 */
@Data
public class ItemInfoOfLargeScreenDTO {

    private Long itemId; // 设备id

    private String itemName; // 设备名称

    private String itemCode; // 设备编码

    private String areaCode; // 所在区域编码

    private String areaName; // 所在区域名称

    private String runParameterCode; // 运行状态参数编码

    private String onlineParameterCode; // 在线状态参数编码

    private String luminanceParameterCode; // 亮度参数编码

    private String volumeParameterCode; // 音量参数编码

    private String capacityParameterCode; // 容量参数编码

    private String storageStatusParameterCode; // 存储状态参数编码

    private String alarmStatus; // 报警状态

}
