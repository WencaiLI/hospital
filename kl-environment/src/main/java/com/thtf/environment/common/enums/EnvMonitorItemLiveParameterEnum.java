package com.thtf.environment.common.enums;

/**
 * @Author: liwencai
 * @Date: 2022/10/27 15:29
 * @Description:
 */
public enum  EnvMonitorItemLiveParameterEnum {

    HJJKWD("HJJKWD","Temp"),
    HJJKZD("HJJKZD","Lux"),
    HJJKSD("HJJKSD","HUMI"),
    HJJKCO("HJJKCO","CO"),
    HJJKCO2("HJJKCO2","CO2");
    private String parameterType;
    private String itemType;

    EnvMonitorItemLiveParameterEnum(String itemType, String parameterType) {
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public static String getParameterType(String itemType){
        for (EnvMonitorItemLiveParameterEnum envMonitorItemLiveParameterEnum : EnvMonitorItemLiveParameterEnum.values()) {
            if(envMonitorItemLiveParameterEnum.itemType.equals(itemType)){
                return envMonitorItemLiveParameterEnum.getParameterType();
            }
        }
        return null;
    }
}
