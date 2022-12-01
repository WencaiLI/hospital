package com.thtf.environment.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.thtf.environment.vo.BroadcastParameterVO;
import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/7 21:37
 * @Description:
 */
@Data
public class ItemInfoOfBroadcastDTO extends BroadcastParameterVO {
    /**
     * 设备id
     */
    @JsonSerialize(using = ToStringSerializer.class)
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
     * 所在区域名称
     */
    private String areaName;

    /**
     * 建筑编码
     */
    private String buildingCode;

    /**
     * ip地址
     */
    private String ipAddress;

//    private String onlineParameterCode; // 在线状态参数编码
//
//    private Object onlineParameterValue; // 在线状态参数编码
//
//    private String taskParameterCode; // 任务状态参数 播放 空闲
//
//    private Object taskParameterValue; // 任务状态参数 播放 空闲
//
//    private String taskQueueParameterCode; // 任务队列参数
//
//    private Object taskQueueParameterValue; // 任务状态参数 播放 空闲
//
//    private String volumeParameterCode; // 音量状态参数
//
//    private Object volumeParameterValue; // 任务状态参数 播放 空闲
//
//    private String audioReceivesParameterCode; // 音频接收参数
//
//    private Object audioReceivesParameterValue; // 任务状态参数 播放 空闲
//
//    private String alarmStatus; // 报警状态

    /**
     * 模型视角
     */
    private List<Integer> eye;

    /**
     * 模型视角
     */
    private List<Integer> center;

    /**
     *
     */
    private List<ParameterInfoDTO> parameterList;
}
