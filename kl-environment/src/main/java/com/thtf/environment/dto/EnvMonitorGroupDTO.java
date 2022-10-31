package com.thtf.environment.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: liwencai
 * @Date: 2022/10/31 15:52
 * @Description:
 */
@Data
public class EnvMonitorGroupDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private String areaName;
    private List<EnvMonitorItemTypeDTO> result;

}
