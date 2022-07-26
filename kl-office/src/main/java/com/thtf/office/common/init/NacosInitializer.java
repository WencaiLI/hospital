package com.thtf.office.common.init;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import java.util.Properties;

/**
 * 初始化本工程在nacos中的配置
 */
public class NacosInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    private static final Log logger = LogFactory.getLog(com.thtf.office.common.init.NacosInitializer.class);

    @Value("${init.nacos.data-id}")
    private String dataId;

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String serverAddr;

    @Value("${init.nacos.group}")
    private String group;

    @Value("${init.nacos.content}")
    private String content;

    @Value("${init.nacos.common.id}")
    private String commonId;

    @Value("${init.nacos.common.content}")
    private String commonContent;


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        logger.info("NacosInitializer initialize 开始执行");
        try {
            intiNacosConfig();
        } catch (NacosException e) {
            logger.error("发布配置失败!", e);
        }
        logger.info("NacosInitializer initialize 执行成功");
    }

    private void intiNacosConfig() throws NacosException {

        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        ConfigService configService = NacosFactory.createConfigService(properties);

        String remoteCommon = configService.getConfig(commonId, group, 3000);
        if(remoteCommon == null) {
            boolean result = configService.publishConfig(commonId, group, commonContent);
            if(result) {
                logger.info("common配置发布成功!");
            }
        }

        String configContent = configService.getConfig(dataId, group, 3000);
        if(configContent == null) {
            boolean result = configService.publishConfig(dataId, group, content);
            if(result) {
                logger.info("application配置发布成功!");
            }
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 5;
    }
}
