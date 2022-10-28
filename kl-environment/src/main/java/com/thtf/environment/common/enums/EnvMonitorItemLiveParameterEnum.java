package com.thtf.environment.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * @Author: liwencai
 * @Date: 2022/10/27 15:29
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum  EnvMonitorItemLiveParameterEnum {

    HJJKWD("HJJKWD_TYPE","Temp"),
    HJJKZD("HJJKZD_TYPE","Lux"),
    HJJKSD("HJJKSD_TYPE","HUMI"),
    HJJKCO("HJJKCO_TYPE","CO"),
    HJJKCO2("HJJKCO2_TYPE","CO2");
    public final String itemType;
    public final String parameterType;

    public static EnvMonitorItemLiveParameterEnum getMonitorItemLiveEnumByTypeCode(String itemType){
        for (EnvMonitorItemLiveParameterEnum envMonitorItemLiveParameterEnum : EnvMonitorItemLiveParameterEnum.values()) {
            if(envMonitorItemLiveParameterEnum.getItemType().equals(itemType)){
                return envMonitorItemLiveParameterEnum;
            }
        }
        throw new IllegalArgumentException("name is invalid");
    }
}
