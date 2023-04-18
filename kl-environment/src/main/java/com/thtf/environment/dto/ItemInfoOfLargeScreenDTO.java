package com.thtf.environment.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/22 21:27
 * @Description: 大屏信息
 */
@Data
public class ItemInfoOfLargeScreenDTO {

    @JsonSerialize(using = ToStringSerializer.class)

    /**
     * 设备id
     */
    private Long itemId;

    /**
     * 设备名称
     */
    private String itemName;

    /**
     * 设备编码
     */
    private String itemCode;

    /**
     * 所在区域编码
     */
    private String areaCode;

    /**
     * 所在区域名称
     */
    private String areaName;

    /**
     * 所在区域编码
     */
    private String buildingCode;


    /**
     * 报警状态
     */
    private Integer alarmStatus;

    /**
     * 模型视角
     */
    private List<Integer> eye;

    /**
     * 模型视角
     */
    private List<Integer> center;

    List<ParameterInfoDTO> parameterList;

}
