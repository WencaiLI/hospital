package com.thtf.face_recognition.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
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
    @JsonSerialize(using = ToStringSerializer.class)
    private Long itemId;

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

    /**
     * 报警级别
     */
    private String alarmLevel;

    /**
     * 告警类型
     */
    private String catchImageUrl;

    private String catchImageTarget;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmTime;

    private String stayTime;

    private List<Integer> eye;

    private List<Integer> center;



}
