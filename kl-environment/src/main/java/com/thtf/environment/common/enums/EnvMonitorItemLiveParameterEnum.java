package com.thtf.environment.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: liwencai
 * @Date: 2022/10/27 15:29
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum  EnvMonitorItemLiveParameterEnum {

    HJJKWD("HJJKWD_TYPE","环境监控温度点","Temp","温度"),
    HJJKZD("HJJKZD_TYPE","环境监控照度点","Lux","光照"),
    HJJKSD("HJJKSD_TYPE","环境监控湿度点","HUMI","湿度"),
    HJJKCO("HJJKCO_TYPE","环境监控CO浓度点","CO","CO"),
    HJJKCO2("HJJKCO2_TYPE","环境监控CO2浓度点","CO2","CO2"),
    SWHJJC("SWHJJC_TYPE","室外温度环境监测点","SWHJJC_TEMPLATE","室外温度");
    public final String itemTypeCode;
    public final String itemTypeName;
    public final String parameterType;
    public final String parameterTypeName;

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

    public static EnvMonitorItemLiveParameterEnum getMonitorItemLiveEnumByParameterTypeName(String parameterTypeName){
        for (EnvMonitorItemLiveParameterEnum envMonitorItemLiveParameterEnum : EnvMonitorItemLiveParameterEnum.values()) {
            if(envMonitorItemLiveParameterEnum.getParameterTypeName().equals(parameterTypeName)){
                return envMonitorItemLiveParameterEnum;
            }
        }
        throw new IllegalArgumentException("name is invalid");
    }
}
