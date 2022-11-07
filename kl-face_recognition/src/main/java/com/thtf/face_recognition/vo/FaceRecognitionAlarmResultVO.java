package com.thtf.face_recognition.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/11/7 10:50
 * @Description:
 */
@Data
public class FaceRecognitionAlarmResultVO {

    /**
     * 设备id
     */
    private String itemId;

    /**
     * 设备编码
     */
    private String itemCode;

    /**
     * 设备名称
     */
    private String itemName;

    /**
     * 设备描述
     */
    private String itemDescription;

    /**
     * 所在区域名称
     */
    private String areaName;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 告警类型
     */
    private String alarmType;

    private String alarmTime;

    private String stayTime;

    private List<Integer> eye;

    private List<Integer> center;



}
