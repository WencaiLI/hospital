package com.thtf.face_recognition.common.config;

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
    @Value("${face-recognition-system.online}")
    private String faceRecognitionOnline;

    /**
     * 相机方位
     */
    @Value("${face-recognition-system.position}")
    private String faceRecognitionPosition;
}
