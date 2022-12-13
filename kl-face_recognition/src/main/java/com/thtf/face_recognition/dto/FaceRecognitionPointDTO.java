package com.thtf.face_recognition.dto;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/12/7 17:26
 * @Description:
 */
@Data
public class FaceRecognitionPointDTO {
    /**
     * 设备编码
     */
    private String itemCode;
    /**
     * 设备名
     */
    private String itemName;

    /**
     * 设备描述
     */
    private String description;

    /**
     * 设备类别编码
     */
    private String itemTypeCode;

    /**
     * 设备类别名称
     */
    private String itemTypeName;

    /**
     * 在线状态编码
     */
    private String onlineParameterCode;

    /**
     * 在线状态值
     */
    private String onlineValue;

    /**
     * 方位
     */
    private String positionParameterCode;

    /**
     * 方位值
     */
    private String positionValue;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 地址
     */
    private String ipAddress;

    /**
     * 通道号
     */
    private String channelNum;
}
