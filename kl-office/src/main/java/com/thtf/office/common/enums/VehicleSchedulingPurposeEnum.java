package com.thtf.office.common.enums;

/**
 * @Author: liwencai
 * @Date: 2023/3/3 11:03
 * @Description:
 */
@SuppressWarnings("all")
public enum VehicleSchedulingPurposeEnum {
    OUT(0,"出车"),
    MAINTAIN(1,"维保"),
    ELIMINATED(2,"淘汰");

    private Integer status;

    private String desc;

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
    VehicleSchedulingPurposeEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
