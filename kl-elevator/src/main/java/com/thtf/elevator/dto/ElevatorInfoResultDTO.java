package com.thtf.elevator.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/5 11:51
 * @Description:
 */
@Data
public class ElevatorInfoResultDTO {
    /**
     * 参数列表
     */
    List<ParameterInfoDTO> parameterList;
    /**
     * 设备id
     */
    private Long itemId;
    /**
     * 设备编码
     */
    private String itemCode;
    /**
     * 设备名称
     */
    private String itemName;
    /**
     * 区域编码
     */
    private String areaCode;
    /**
     * 区域名称
     */
    private String areaName;
    /**
     * 建筑编码
     */
    private String buildingCode;
    /**
     * 建筑名称
     */
    private String buildingName;
    /**
     * 报警状态
     */
    private String alarmCategory;
}
