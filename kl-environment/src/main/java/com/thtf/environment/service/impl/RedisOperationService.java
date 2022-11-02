package com.thtf.environment.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thtf.common.util.IdGeneratorSnowflake;
import com.thtf.environment.dto.ItemPlayInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    private final static String REMOTE_KEY = "_remote_key";

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

    @Transactional
    public Boolean remoteSwitchItemStatusByItemCodeList(List<String> itemCodeList){
        for (String itemCode : itemCodeList) {
            Integer valueInRedis = (Integer)redisTemplate.opsForHash().get(APPLICATION_NAME + REMOTE_KEY, itemCode);
            if(null == valueInRedis || 0 == valueInRedis){ // 当处理默认设备编码
                redisTemplate.opsForHash().put(APPLICATION_NAME+REMOTE_KEY,itemCode,1); // 将其转换为被控制状态
            }else if(1 == valueInRedis){
                redisTemplate.opsForHash().put(APPLICATION_NAME+REMOTE_KEY,itemCode,0);
            }else {
                log.error("出错");
            }
        }
        return true;
    }

    @Transactional
    public Boolean insertPlayOrder(ItemPlayInfoDTO param) {
        // String key = APPLICATION_NAME+"_PLAY_ORDER";
        Long playOrderId =  idGeneratorSnowflake.snowflakeId();
        param.setId(playOrderId);
        if(StringUtils.isBlank(param.getItemCode())){
            return false;
        }
        List<ItemPlayInfoDTO> playOrderByItemCode = this.getPlayOrderByItemCode(param.getItemCode());
        if(null == playOrderByItemCode) {
            playOrderByItemCode = new ArrayList<>();
        }
        playOrderByItemCode.add(param);
        redisTemplate.opsForHash().put(APPLICATION_NAME + "_play_order", param.getItemCode(),playOrderByItemCode);
        return true;
    }


    public List<ItemPlayInfoDTO> getPlayOrderByItemCode(String itemCode) {
        List<ItemPlayInfoDTO> resultList = new ArrayList<>();
        Object objectInRedis = redisTemplate.opsForHash().get(APPLICATION_NAME + "_play_order", itemCode);
        if(null == objectInRedis){
            return null;
        }else {
            for (Object object : (List<Object>)objectInRedis) {
                System.out.println(object);
                // 将list中的数据转成json字符串
                String jsonObject= JSON.toJSONString(object);
                //将json转成需要的对象
                ItemPlayInfoDTO itemPlayInfoDTO = JSONObject.parseObject(jsonObject,ItemPlayInfoDTO.class);
                resultList.add(itemPlayInfoDTO);
                System.out.println(itemPlayInfoDTO);
            }
        }
        return resultList;
    }
}
