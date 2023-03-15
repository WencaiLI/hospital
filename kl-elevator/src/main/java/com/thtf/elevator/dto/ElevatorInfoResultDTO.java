package com.thtf.elevator.dto;

import com.thtf.elevator.vo.ElevatorStatusVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/5 11:51
 * @Description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ElevatorInfoResultDTO extends ElevatorStatusVO {
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
    private String alarmStatus;

    /**
     * 参数列表
     */
    List<ParameterInfoDTO> parameterList;
}
