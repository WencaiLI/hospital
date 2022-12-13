package com.thtf.face_recognition.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: liwencai
 * @Date: 2022/12/8 09:13
 * @Description:
 */
@Data
public class MegviiItemEventDTO {
    /**
     * 用户名
     */
    private String personName;

    /**
     * 访客
     */
    private String personType;

    /**
     * 身份证号
     */
    private String identifyNum;

    /**
     * 事件区域
     */
    private String eventArea;

    /**
     * 电话
     */
    private String phone;

    /**
     * 事件发生时间
     */
    private LocalDateTime eventTime;

    /**
     * 抓拍图
     */
    private String captureImageUrl;

    /**
     * 用户url
     */
    private String personImageUri;

    /**
     * 事件id
     */
    private String eventName;

    /**
     * 事件id
     */
    private String eventType;

}
