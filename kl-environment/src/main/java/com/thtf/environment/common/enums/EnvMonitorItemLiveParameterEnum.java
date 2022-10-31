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

    HJJKWD("HJJKWD_TYPE","环境监控温度点","Temp"),
    HJJKZD("HJJKZD_TYPE","环境监控照度点","Lux"),
    HJJKSD("HJJKSD_TYPE","环境监控湿度点","HUMI"),
    HJJKCO("HJJKCO_TYPE","环境监控CO浓度点","CO"),
    HJJKCO2("HJJKCO2_TYPE","环境监控CO2浓度点","CO2");
    public final String itemTypeCode;
    public final String itemTypeName;
    public final String parameterType;

    public static EnvMonitorItemLiveParameterEnum getMonitorItemLiveEnumByTypeCode(String itemType){
        for (EnvMonitorItemLiveParameterEnum envMonitorItemLiveParameterEnum : EnvMonitorItemLiveParameterEnum.values()) {
            if(envMonitorItemLiveParameterEnum.getItemTypeCode().equals(itemType)){
                return envMonitorItemLiveParameterEnum;
            }
        }
        throw new IllegalArgumentException("name is invalid");
    }

    public static EnvMonitorItemLiveParameterEnum getMonitorItemLiveEnumByParameterType(String ParameterType){
        for (EnvMonitorItemLiveParameterEnum envMonitorItemLiveParameterEnum : EnvMonitorItemLiveParameterEnum.values()) {
            if(envMonitorItemLiveParameterEnum.getParameterType().equals(ParameterType)){
                return envMonitorItemLiveParameterEnum;
            }
        }
        throw new IllegalArgumentException("name is invalid");
    }
}
