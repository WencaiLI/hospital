package com.thtf.office.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Auther: liwencai
 * @Date: 2022/7/26 17:19
 * @Description: 移除绑定车辆传参ViewObject
 */
@Data
public class VehicleCategoryChangeBindVO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long originId;      // 源类别id

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long targetId;      // 目的类别id

    @JsonSerialize(using = ToStringSerializer.class)
    private List<String> vidList;     // 需要移除的公车id集
}
