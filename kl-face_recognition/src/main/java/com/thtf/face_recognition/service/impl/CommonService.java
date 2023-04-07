package com.thtf.face_recognition.service.impl;

import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.common.feign.ItemAPI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: liwencai
 * @Date: 2023/3/15 17:13
 * @Description:
 */
@Service
@Slf4j
public class CommonService {

    @Autowired
    private ItemAPI itemAPI;

    public String getParameterValueByStateExplain(String systemCode,String parameterTypeCode,String itemTypeCodes,String[] keywords){
        Map<String, String> map = itemAPI.listParameterStateExplainBySysCode(parameterTypeCode,systemCode,itemTypeCodes).getData();
        System.out.println(map);
        if(null != map){
            String stateExplain = map.get(parameterTypeCode);
            System.out.println("parameterTypeCode "+parameterTypeCode+"  stateExplain "+stateExplain);
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

    public String getParameterValueByStateExplain(String parameterCode, List<TblItemParameter> parameterList, String[] keywords) {
        for (TblItemParameter parameter : parameterList) {
            if(parameter.getParameterType().equals(parameterCode)){
                for (String keyword : keywords) {
                    if(parameter.getStateExplain().contains(keyword)){
                        String[] split = parameter.getStateExplain().split(";");
                        for (String s : split) {
                            String[] split1 = s.split(":");
                            if(split1.length == 2){
                                if(split1[1].contains(keyword)){
                                    return split1[0];
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

}

