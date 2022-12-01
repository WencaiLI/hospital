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

    /**
     * 设备id
     */
    private Long itemId;

    /**
     * 设备名称
     */
    private String itemName;

    /**
     * 设备编码
     */
    private String itemCode;

    /**
     * 所在区域编码
     */
    private String areaCode;

    /**
     * 所在区域编码
     */
    private String buildingCode;

    /**
     * 所在区域名称
     */
    private String areaName;

    /**
     * 运行状态参数编码
     */
     private String runParameterCode;

    /**
     * 当前运行值
     */
    private String runValue;

    /**
     * 在线状态参数编码
     */
     private String onlineParameterCode;

    /**
     * 当前在线状态
     */
    private String onlineValue;

    /**
     * 亮度参数编码
     */
     private String luminanceParameterCode;

    /**
     * 当前亮度参数
     */
    private String luminanceValue;

    /**
     * 音量参数编码
     */
     private String volumeParameterCode;

    /**
     * 当前音量参数
     */
    private String volumeValue;

    /**
     * 容量参数编码
     */
     private String capacityParameterCode;

    /**
     * 当前音量参数
     */
    private String capacityValue;

    /**
     * 存储状态参数编码
     */
     private String storageStatusParameterCode;

    /**
     * 当前音量参数
     */
    private String storageStatusValue;

    /**
     * 放映时长参数编码
     */
     private String showDurationParameterCode;

    /**
     * 放映时长值
     */
    private String showDurationValue;

    /**
     * 报警状态
     */
    private Integer alarmStatus;

    /**
     * 模型视角
     */
    private List<Integer> eye;

    /**
     * 模型视角
     */
    private List<Integer> center;

    List<ParameterInfoDTO> parameterList;

}
