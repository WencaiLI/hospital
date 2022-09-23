package com.thtf.environment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author: liwencai
 * @Date: 2022/9/21 17:19
 * @Description: 信息发布接口
 */
@Configuration
public class RedisAutoConfig {
    @Bean
    public RedisTemplate<String,Object> redisTemplate4String(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return  redisTemplate;
    }
}