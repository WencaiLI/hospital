package com.thtf.office.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.thtf.office.common.valid.VehicleParamValid;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Auther: liwencai
 * @Date: 2022/7/26 17:31
 * @Description:
 */
@Data
public class VehicleInfoParamVO implements Serializable {

    @NotNull(groups = {VehicleParamValid.Update.class})
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id; // 公车id

    @NotBlank(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class})
    private String carNumber; // 车牌号

    @NotNull(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class})
    @JsonSerialize(using = ToStringSerializer.class)
    private Long vehicleCategoryId; // 联的车辆类别id

    @NotBlank(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class})
    private String model; // 厂牌型号

    @NotBlank(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class})
    private String engineNumber; // 发动机号

    @NotBlank(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class})
    private String frameNumber; // 车架号

    private String color; // 车身颜色

    private String carImage; // 车辆照片名称

    private String carImageUrl; // 车辆照片url

    private String drivingBookImage; // 车辆行驶本照片名称

    private String drivingBookImageUrl; // 车辆行驶本照片url

    private String distributor; // 经销商

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime outDate; // 出厂日期

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime buyDate; // 购买日期

    private BigDecimal price; // 购买价格

    private String insurance; // 保险说明

    private String maintenance; // 维保说明

    private String description; // 描述

    private Integer status; // 车辆状态 0：待命中；1：出车中；2：维修中；3：已淘汰
}
