package com.thtf.environment.service.impl;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.thtf.common.util.IdGeneratorSnowflake;
import com.thtf.environment.dto.ItemPlayInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/23 14:34
 * @Description: 有关redis的相关操作
 */
@Service
@Slf4j
public class RedisOperationService {
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.application.name}")
    private String APPLICATION_NAME;

    private final static String BUILDING_AREA_KEY = "_building_area_name";

    private final static String REMOTE_KEY = "remote_key";

    @Autowired
    IdGeneratorSnowflake idGeneratorSnowflake;

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


    public Boolean remoteSwitchItemStatusByItemIdList(List<Long> itemIdList){
        for (Long itemId : itemIdList) {
            Integer valueInRedis = (Integer)redisTemplate.opsForHash().get(APPLICATION_NAME + REMOTE_KEY, itemId);
            if(null == valueInRedis || 0 == valueInRedis){ // 当处理默认设备编码
                redisTemplate.opsForHash().put(APPLICATION_NAME+REMOTE_KEY,itemId,1); // 将其转换为被控制状态
            }else if(1 == valueInRedis){
                redisTemplate.opsForHash().put(APPLICATION_NAME+REMOTE_KEY,itemId,0);
            }else {
                log.error("出错");
            }
        }
        return true;
    }

    public Boolean insertPlayOrder(ItemPlayInfoDTO param) {
//        String key = APPLICATION_NAME+"_PLAY_ORDER";
//        String property
//        Long playOrderId =  idGeneratorSnowflake.snowflakeId();
//
//        param.setId(property);
        return true;
    }
}
