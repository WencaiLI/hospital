package com.thtf.office.common.enums;

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
    ELIMINATED(3,"被淘汰");

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


}
