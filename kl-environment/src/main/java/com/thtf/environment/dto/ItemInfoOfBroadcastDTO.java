package com.thtf.environment.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.thtf.environment.vo.BroadcastParameterVO;
import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/7 21:37
 * @Description:
 */
@Data
public class ItemInfoOfBroadcastDTO  {
    /**
     * 设备id
     */
    @JsonSerialize(using = ToStringSerializer.class)
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
     * 建筑编码
     */
    private String buildingCode;

    /**
     * ip地址
     */
    private String ipAddress;

    /**
     * 模型视角
     */
    private List<Integer> eye;

    /**
     * 模型视角
     */
    private List<Integer> center;

    /**
     *
     */
    private List<ParameterInfoDTO> parameterList;
}
