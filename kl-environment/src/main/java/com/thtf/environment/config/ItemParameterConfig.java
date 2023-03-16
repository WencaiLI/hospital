package com.thtf.environment.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @Author: liwencai
 * @Date: 2023/3/15 17:15
 * @Description:
 */
@RefreshScope
@Getter
@Component
public class ItemParameterConfig {
    /**
     * 开关状态
     */
    @Value("${STATE}")
    private String state;

    /**
     * 报警状态
     */
    @Value("${ALARM}")
    private String alarm;

    /**
     * 故障状态
     */
    @Value("${FAULT}")
    private String fault;

    /**
     * 信息发布在线
     */
    @Value("${infoPublish-system.online}")
    private String infoPublishOnline;

    /**
     * 大屏亮度
     */
    @Value("${infoPublish-system.luminance}")
    private String infoPublishLuminance;

    /**
     * 大屏音量
     */
    @Value("${infoPublish-system.volume}")
    private String infoPublishVolume;

    /**
     * 运行时长
     */
    @Value("${infoPublish-system.runTime}")
    private String infoPublishRunTime;

    /**
     * 总容量
     */
    @Value("${infoPublish-system.capacity}")
    private String infoPublishCapacity;

    /**
     * 已使用容量
     */
    @Value("${infoPublish-system.storedCapacity}")
    private String infoPublishStoredCapacity;

    /**
     * 在线状态
     */
    @Value("${broadcast-system.online}")
    private String broadcastOnline;

    /**
     * 广播任务状态
     */
    @Value("${broadcast-system.taskStatus}")
    private String broadcastTaskStatus;

    /**
     * 任务队列
     */
    @Value("${broadcast-system.taskQueue}")
    private String broadcastTaskQueue;

    /**
     * 音量
     */
    @Value("${broadcast-system.audio}")
    private String broadcastAudio;

    /**
     * 音量控制
     */
    @Value("${broadcast-system.audioCtrl}")
    private String broadcastAudioCtrl;

    /**
     * 对讲状态
     */
    @Value("${broadcast-system.intercomStatus}")
    private String broadcastIntercomStatus;

    /**
     * 播报端口
     */
    @Value("${broadcast-system.playPort}")
    private String broadcastPlayPort;
}
