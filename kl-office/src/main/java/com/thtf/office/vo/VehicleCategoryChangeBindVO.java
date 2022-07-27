package com.thtf.office.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: liwencai
 * @Date: 2022/7/26 17:19
 * @Description:
 */
@Data
public class VehicleCategoryChangeBindVO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long originId;      // 源类别id

    @JsonSerialize(using = ToStringSerializer.class)
    private Long targetId;      // 目的类别id

    @JsonSerialize(using = ToStringSerializer.class)
    private Long[] vidList;     // 需要移除的公车id集
}
