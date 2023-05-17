package com.thtf.face_recognition.application.dto;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2023/2/21 17:48
 * @Description:
 */
@Data
public class MegviiPTZControlParam {

    /**
     * 设备uuid
     */
    private String deviceUuid;

    /**
     * 云台控制命令:1
     * <上;2<下:3<左:4<右:5<左上:6<右上;7<左下:8<右下:9<焦距变大(变倍+)
     * :10<焦距变小(变倍-) :11<焦点前调(调焦+)
     * :12<焦点后调 (调焦-) :13<光圈扩大:14<光圈缩小< span="“>
     */
    private Integer ptzCmd;

    /**
     * 速度:控制云台、镜头的速度，范围为[1-81
     */
    private Integer speed;

    /**
     * 停止或者开始动作:云台停止动作或开始动作:0-开始，非0-停止
     */
    private Integer stop;
}
