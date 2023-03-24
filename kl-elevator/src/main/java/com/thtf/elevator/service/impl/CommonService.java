package com.thtf.elevator.service.impl;

import com.thtf.common.feign.ItemAPI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author: liwencai
 * @Date: 2023/3/14 15:32
 * @Description: 通用方法
 */
@Service
@Slf4j
public class CommonService {

    @Autowired
    private ItemAPI itemAPI;

    public String getParameterValueByStateExplain(String systemCode,String parameterTypeCode,String itemTypeCodes,String[] keywords){
        Map<String, String> map = itemAPI.listParameterStateExplainBySysCode(parameterTypeCode,systemCode,itemTypeCodes).getData();
        if(null != map){
            String stateExplain = map.get(parameterTypeCode);
            if(StringUtils.isNotBlank(stateExplain)){
                String[] split = stateExplain.split(";");
                for (String explain :split) {
                    String[] valueAndWords = explain.split(":");
                    if(valueAndWords.length == 2){
                        for (String keyWord: keywords){
                            if(valueAndWords[1].contains(keyWord)){
                                return valueAndWords[0];
                            }
                        }
                    }
                }

            }
        }
        return null;
    }
}
