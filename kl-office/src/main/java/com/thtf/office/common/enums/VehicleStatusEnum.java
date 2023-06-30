package com.thtf.office.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: liwencai
 * @Date: 2023/3/3 10:45
 * @Description:
 */
@SuppressWarnings("all")
public enum VehicleStatusEnum {
    STANDBY(0,"待命中"),
    OUT(1,"出车中"),
    MAINTAIN(2,"维修中"),
    ELIMINATED(3,"已淘汰");

    private Integer status;

    private String desc;

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
    VehicleStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    /**
     * 根据状态获取中文状态
     *
     * @param status
     * @return {@link String}
     * @author liwencai
     */
    public static String getDescByStatus(Integer status){
        for (VehicleStatusEnum value : VehicleStatusEnum.values()) {
            if(value.getStatus().equals(status)){
                return value.getDesc();
            }
        }
        return null;
    }

    public static String getDescByStatus(String status){
        if(StringUtils.isEmpty(status)) {
            return null;
        }
        for (VehicleStatusEnum value : VehicleStatusEnum.values()) {
            if(value.getStatus().equals(Integer.parseInt(status))){
                return value.getDesc();
            }
        }
        return null;
    }

}
