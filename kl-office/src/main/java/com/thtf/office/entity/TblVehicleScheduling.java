package com.thtf.office.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 车辆调度表
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TblVehicleScheduling implements Serializable {

private static final long serialVersionUID = 1L;

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
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 删除时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime deleteTime;

    /**
     * 删除人
     */
    private String deleteBy;

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
    private Integer purpose;

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
     * 调度状态 0 调度尚未结束 1 调度结束
     */
    private Integer status;

}
