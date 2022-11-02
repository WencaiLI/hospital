package com.thtf.environment.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/22 21:27
 * @Description: 大屏信息
 */
@Data
public class ItemInfoOfLargeScreenDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long itemId; // 设备id

    private String itemName; // 设备名称

    private String itemCode; // 设备编码

    private String areaCode; // 所在区域编码

    private String buildingCode; // 所在区域编码

    private String areaName; // 所在区域名称

    private String runParameterCode; // 运行状态参数编码

    private Object runValue; // 当前运行值

    private String onlineParameterCode; // 在线状态参数编码

    private Object onlineValue; // 当前在线状态

    private String luminanceParameterCode; // 亮度参数编码

    private Object luminanceValue; // 当前亮度参数

    private String volumeParameterCode; // 音量参数编码

    private Object volumeValue; // 当前音量参数

    private String capacityParameterCode; // 容量参数编码

    private Object capacityValue; // 当前音量参数

    private String storageStatusParameterCode; // 存储状态参数编码

    private Object storageStatusValue; // 当前音量参数

    private String showDurationParameterCode; // 放映时长参数编码

    private Object showDurationValue; // 放映时长值

    private String alarmStatus; // 报警状态

    private List<Integer> eye; // 相机视角

    private List<Integer> center; // 近距离视角

}
