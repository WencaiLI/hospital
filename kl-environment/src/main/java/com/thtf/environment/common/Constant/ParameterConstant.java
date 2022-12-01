package com.thtf.environment.common.Constant;

/**
 * @Author: liwencai
 * @Date: 2022/11/3 10:57
 * @Description:
 */
public class ParameterConstant {
    /* ********************* 公共广播 ******************** */
    /**
     * 公共广播参数类别编码 - 在线状态
     */
    public final static String BROADCAST_ONLINE = "OnlineStatus";

    /**
     * 公共广播参数类别编码 - 运行状态
     */
    public final static String BROADCAST_STATE = "State";

    /**
     * 公共广播参数类别编码 - 运行状态 -运行
     */
    public final static String ON_OFF_STATUS_ON_VALUE = "1";

    /**
     * 公共广播参数类别编码 - 运行状态 -停止
     */
    public final static String ON_OFF_STATUS_OFF_VALUE = "0";

    /**
     * 公共广播参数类别编码 - 广播任务状态
     */
    public final static String BROADCAST_TASK_STATUS = "TaskStatus";

    /**
     * 公共广播参数类别编码 - 音量状态
     */
    public final static String BROADCAST_AUDIO = "Audio";

    /**
     * 公共广播参数类别编码 - 音量控制
     */
    public final static String BROADCAST_AUDIO_CONTROL = "AudioCtrl";

    public final static String BROADCAST_TASK_QUEUE = "TaskQueue";

    /**
     * 公共广播参数类别编码 - 报警
     */
    public final static String BROADCAST_ALARM = "Alarm";

    /**
     * 公共广播参数类别编码 - 故障
     */
    public final static String BROADCAST_FAULT = "Fault";

    /**
     * 公共广播参数类别编码 - 对讲状态编码
     */
    public final static String BROADCAST_INTERCOM_STATUS = "IntercomStatus";

    /**
     * 公共广播参数类别编码 - 消防播报
     */
    public final static String BROADCAST_PLAY_PORT = "PlayPort";

    /**
     * 运行状态值为“运行”的值
     */
    public final static String BROADCAST_TASK_ON_VALUE = "1";

    /**
     * 运行状态为“关闭”的值
     */
    public final static String BROADCAST_TASK_OFF_VALUE = "0";


    /* ********************* 信息发布 ******************** */
    /**
     * 信息发布信息参数类别编码 - 运行状态
     */
    public final static String INFO_PUBLISH_RUN_STATUS = "State";

    /**
     * 信息发布信息参数类别编码 - 在线状态
     */
    public final static String INFO_PUBLISH_ONLINE_STATUS = "OnlineStatus";

    /**
     * 硬盘容量
     */
    public final static String INFO_PUBLISH_CAPACITY = "Capacity";

    /**
     * 已存储容量
     */
    public final static String INFO_PUBLISH_STORED_CAPACITY = "StoredCapacity";

    /**
     * 亮度
     */
    public final static String INFO_PUBLISH_LUMINANCE = "Luminance";

    /**
     * 音量
     */
    public final static String INFO_PUBLISH_VOLUME = "Volume";

    /**
     * 放映时长
     */
    public final static String INFO_PUBLISH_RUN_TIME = "RunTime";
}
