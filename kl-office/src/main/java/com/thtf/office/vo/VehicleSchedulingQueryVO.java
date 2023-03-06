package com.thtf.office.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: liwencai
 * @Date: 2023/2/23 19:03
 * @Description:
 */
@Data
public class VehicleSchedulingQueryVO {
    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 调度流水号
     */
    private String code;

    /**
     * 关联的车辆类别id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long vehicleCategoryId;

    /**
     * 关联的车辆类别名称
     */
    private String vehicleCategoryName;

    /**
     * 描述
     */
    private String description;

    /**
     * 关联的车辆信息id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long vehicleInfoId;

    /**
     * 车牌号
     */
    private String carNumber;

    /**
     * 调度开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 调度结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime endTime;

    /**
     * 司机
     */
    private String driverName;

    /**
     * 调度用途 0：出车；1：维保；2：淘汰
     */
    private String purpose;

    /**
     * 关联的使用部门id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long organizationId;

    /**
     * 使用人姓名
     */
    private String userName;

    /**
     * 目的地
     */
    private String destination;

    /**
     * 司机关联的用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long driverId;

    /**
     * 关联的使用部门名称
     */
    private String organizationName;

    /**
     * 调度状态 0 调度尚未结束 1 调度结束 2 尚未开始调度
     */
    private Integer status;

    /**
     * 车辆使用时长
     */
    private Long workingDuration;
}
