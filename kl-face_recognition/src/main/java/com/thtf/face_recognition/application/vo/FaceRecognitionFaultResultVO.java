package com.thtf.face_recognition.application.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/12/7 15:35
 * @Description:
 */
@Data
public class FaceRecognitionFaultResultVO {

    /**
     * 设备编码
     */
    private String itemCode;

    /**
     * 设备名称
     */
    private String itemName;

    /**
     * 区域编码
     */
    private String areaCode;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * ip地址
     */
    private String ipAddress;

    /**
     * 报警级别
     */
    private Integer alarmLevel;

    /**
     * 报警时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime alarmTime;

    /**
     * 留置时长
     */
    private String stayTime;

    /**
     * 模型相机位置
     */
    private List<Integer> eye;

    /**
     * 模型相机位置
     */
    private List<Integer> center;
}
