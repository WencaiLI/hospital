package com.thtf.office.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.thtf.office.common.valid.VehicleParamValid;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: liwencai
 * @Date: 2022/7/26 17:31
 * @Description:
 */
@Data
public class VehicleInfoParamVO implements Serializable {

    @NotNull(groups = {VehicleParamValid.Update.class})
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id; // 公车id

    // 异常BindException
    @Pattern(regexp = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-HJ-NP-Z][A-HJ-NP-Z0-9]{4,5}[A-HJ-NP-Z0-9挂学警港澳]$",
            groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class},message = "请正确填写车牌号！")
    private String carNumber; // 车牌号

    @NotNull(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class},message = "请填写车辆类别！")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long vehicleCategoryId; // 联的车辆类别id

    @NotBlank(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class},message = "请填写厂牌号！")
    private String model; // 厂牌型号

    @NotBlank(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class},message = "请填写发动机号！")
    private String engineNumber; // 发动机号

    @NotBlank(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class},message = "请填写车架号！")
    private String frameNumber; // 车架号

    private String color; // 车身颜色

    private String carImage; // 车辆照片名称

    private String carImageUrl; // 车辆照片url

    private String drivingBookImage; // 车辆行驶本照片名称

    private String drivingBookImageUrl; // 车辆行驶本照片url

    private String distributor; // 经销商

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime outDate; // 出厂日期

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime buyDate; // 购买日期

    private BigDecimal price; // 购买价格

    private String insurance; // 保险说明

    private String maintenance; // 维保说明

    private String description; // 描述

    private Integer status; // 车辆状态 0：待命中；1：出车中；2：维修中；3：已淘汰

    private Integer pageNumber;

    private Integer pageSize;

    private String keyword;

    private String carNumberKeyword;

    private String modelKeyword;
}
