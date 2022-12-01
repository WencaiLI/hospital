package com.thtf.environment.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/12/1 09:14
 * @Description:
 */
@Data
public class BroadcastParameterVO {

    /**
     * 参数编码 - 在线状态
     */
    private String onlineParameterCode;

    /**
     * 参数当前值 - 在线状态（0离线 1在线）
     */
    private String onlineValue;

    /**
     * 运行状态编码
     */
    private String runParameterCode;

    /**
     * 运行状态
     */
    private String runValue;


    /**
     * 参数编码 - 任务状态
     */
    private String taskStatusParameterCode;

    /**
     * 参数当前值 - 任务状态
     */
    private String taskStatusValue;

    /**
     * 参数编码 - 音量状态编码
     */
    private String audioParameterCode;

    /**
     * 参数当前值 - 音量
     */
    private String audioValue;

    /**
     * 参数编码 - 音频接收类型
     */
    private String audioReceiveParameterCode;

    /**
     * 参数编码 - 音频接收值
     */
    private String audioReceiveValue;

    /**
     * 参数编码 - 播放队列数量
     */
    private String taskQueueParameterCode;

    /**
     * 参数当前值 - 播放队列数量
     */
    private String taskQueueValue;

    /**
     * 参数编码 - 报警状态
     */
    private String alarmParameterCode;

    /**
     * 参数当前值 - 报警状态值
     */
    private String alarmValue;

    /**
     * 参数编码 - 故障状态编码
     */
    private String faultParameterCode;

    /**
     * 参数当前值 - 故障状态值
     */
    private String faultValue;

}
