package com.thtf.environment.vo;

import lombok.Data;

/**
 * @author liwencai
 * @since 2023/4/18
 */
@Data
public class InfoPublishParameterVO {


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
}
