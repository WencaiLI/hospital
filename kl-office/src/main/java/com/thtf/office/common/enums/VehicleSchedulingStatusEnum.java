package com.thtf.office.common.enums;

/**
 * @Author: liwencai
 * @Date: 2023/3/3 10:57
 * @Description:
 */
@SuppressWarnings("all")
public enum VehicleSchedulingStatusEnum {
    IN_SCHEDULING(0,"调度中"),
    END_OF_SCHEDULING(1,"调度结束"),
    NOT_START_SCHEDULING(2,"未开始调度");

    private Integer status;

    private String desc;


    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    VehicleSchedulingStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
