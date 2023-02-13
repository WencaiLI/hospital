package com.thtf.office.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.thtf.office.common.valid.VehicleParamValid;
import com.thtf.office.entity.TblVehicleScheduling;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

/**
 * @Author: liwencai
 * @Date: 2022/7/26 21:17
 * @Description:
 */
@Data
public class VehicleSchedulingParamVO {

    /**
     * 调度id
     */
    @NotNull(groups = {VehicleParamValid.Update.class})
    @Null(groups = {VehicleParamValid.Insert.class})
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 调度流水号
     */
    private String code;

    /**
     * 关联的车辆类别id
     */
    @NotNull(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class})
    @JsonSerialize(using = ToStringSerializer.class)
    private Long vehicleCategoryId;

    /**
     * 关联的车辆类别名称
     */
    private String vehicleCategoryName;

    /**
     * 关联的车辆信息id
     */
    @NotNull(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class})
    @JsonSerialize(using = ToStringSerializer.class)
    private Long vehicleInfoId;

    /**
     * 车牌号
     */
    @NotBlank(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class})
    private String carNumber;

    /**
     * 调度开始时间
     */
    @NotNull(groups = {VehicleParamValid.Insert.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 调度结束时间
     */
    @NotNull(groups = {VehicleParamValid.Insert.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime endTime;

    /**
     * 调度用途 0：出车；1：维保；2：淘汰
     */
    @NotNull(groups = {VehicleParamValid.Insert.class})
    private Integer purpose;

    /**
     * 关联的使用部门id
     */
    @NotNull(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class})
    @JsonSerialize(using = ToStringSerializer.class)
    private Long organizationId;

    /**
     * 使用人姓名
     */
    @NotBlank(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class})
    private String userName;

    /**
     * 司机关联的用户id
     */
    @NotNull(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class})
    @JsonSerialize(using = ToStringSerializer.class)
    private Long driverId;

    /**
     * 司机
     */
    @NotBlank(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class})
    private String driverName;

    /**
     * 关联的使用部门名称
     */
    @NotBlank(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class})
    private String organizationName;

    /**
     * 描述
     */
    private String description;

    /**
     * 目的地
     */
    private String destination;

    /**
     * 关键词
     */
    private String keyword;

    private String keyCarNumber;

    private String keyDescription;

    private String keyDestination;

    private Integer pageNumber;

    private Integer pageSize;
}
