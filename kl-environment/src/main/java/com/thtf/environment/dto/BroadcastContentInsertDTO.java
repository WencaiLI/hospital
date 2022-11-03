package com.thtf.environment.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/11/3 15:51
 * @Description:
 */
@Data
public class BroadcastContentInsertDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String itemCode;
    private String broadcastContentId;
    private Object detail;
}
