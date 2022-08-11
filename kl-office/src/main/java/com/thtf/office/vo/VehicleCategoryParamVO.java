package com.thtf.office.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.thtf.office.common.valid.VehicleParamValid;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: liwencai
 * @Date: 2022/7/26 16:00
 * @Description: 新增和修改公车分类使用
 */
@Data
public class VehicleCategoryParamVO implements Serializable {

    @NotNull(groups = {VehicleParamValid.Update.class})
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id; // 公车类别id

    @NotBlank(groups = {VehicleParamValid.Insert.class, VehicleParamValid.Update.class})
    private String name; // 公车类别名臣

    private String description; // 公车类别描述
}
