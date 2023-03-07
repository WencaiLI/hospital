package com.thtf.office.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thtf.office.entity.TblVehicleInfo;

import java.time.LocalDateTime;

/**
 * @Author: liwencai
 * @Date: 2023/3/7 10:59
 * @Description:
 */
public class VehicleInfoVO extends TblVehicleInfo {

    /**
     * 创建人
     */
    @JsonIgnore
    private String createBy;

    /**
     * 修改时间
     */
    @JsonIgnore
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @JsonIgnore
    private String updateBy;

    /**
     * 删除时间
     */
    @JsonIgnore
    private LocalDateTime deleteTime;

    /**
     * 删除人
     */
    @JsonIgnore
    private String deleteBy;
}
