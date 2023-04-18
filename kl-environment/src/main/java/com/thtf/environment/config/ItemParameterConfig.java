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
     * 在线状态
     */
    @Value("${broadcast-system.online}")
    private String broadcastOnline;

}
