package com.thtf.environment.service.impl;

/**
 * @Author: liwencai
 * @Date: 2022/9/23 14:34
 * @Description: 有关redis的相关操作
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class RedisOperationService {
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.application.name}")
    private String APPLICATION_NAME;

    private final static String BUILDING_AREA_KEY = "_building_area_name";

    /**
     * @Author: liwencai
     * @Description: redis中缓存建筑区域编码对建筑名称映射
     * @Date: 2022/9/23
     * @return: java.lang.Boolean
     */
    public Boolean saveBuildAreaCodeMapToName(String areaCode,String areaName){
        try {
            redisTemplate.opsForHash().put(APPLICATION_NAME+BUILDING_AREA_KEY,areaCode,areaName);
            log.info("缓存进redis区域对照表 {} 的信息为：{}，{}",APPLICATION_NAME+BUILDING_AREA_KEY,areaCode,areaName);
            return true;
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * @Author: liwencai
     * @Description: redis中缓存建筑区域编码对建筑名称映射
     * @Date: 2022/9/23
     * @return: java.lang.Boolean
     */
    public String getBuildAreaNameByCode(String areaCode){
        try {
            return (String) redisTemplate.opsForHash().get(APPLICATION_NAME+BUILDING_AREA_KEY,areaCode);
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }
}
