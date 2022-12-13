package com.thtf.face_recognition.common.constant;

/**
 * @Author: liwencai
 * @Date: 2022/12/6 21:52
 * @Description:
 */

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "megvii.server")
public class MegviiConfig {
    @Value("url")
    private String baseUrl;
}
