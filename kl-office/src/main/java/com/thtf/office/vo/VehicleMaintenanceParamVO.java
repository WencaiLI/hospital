package com.thtf.office.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.thtf.office.common.valid.VehicleParamValid;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: liwencai
 * @Date: 2022/7/26 20:49
 * @Description:
 */
@Data
public class VehicleMaintenanceParamVO {

    /**
     *  维保id
     */
    @NotNull(groups = {VehicleParamValid.Update.class})
    @Null(groups = {VehicleParamValid.Insert.class})
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 车辆id
     */
    @NotNull(groups = {VehicleParamValid.Update.class,VehicleParamValid.Insert.class})
    @JsonSerialize(using = ToStringSerializer.class)
    private Long vehicleInfoId;

    /**
     * 名称
     */
    private String name;

    /**
     * 维保时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime maintenanceTime;

    /**
     * 产生费用
     */
    private BigDecimal moneySpent;

    /**
     * 经办人
     */
    private String handledBy;

    /**
     * 描述
     */
    private String description;

    private Integer pageNumber;

    private Integer pageSize;

}
