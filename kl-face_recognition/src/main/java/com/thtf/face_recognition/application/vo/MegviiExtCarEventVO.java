package com.thtf.face_recognition.application.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/12/6 22:22
 * @Description: 车辆事件
 */
@Data
public class MegviiExtCarEventVO {

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 车牌颜色code
     */
    private String plateColor;

    /**
     * 车牌颜色描述
     */
    private String plateColorMsg;

    /**
     * 车主姓名
     */
    private String personName;
}
