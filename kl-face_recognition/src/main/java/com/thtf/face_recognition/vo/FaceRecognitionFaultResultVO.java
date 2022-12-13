package com.thtf.face_recognition.vo;

import lombok.Data;

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
