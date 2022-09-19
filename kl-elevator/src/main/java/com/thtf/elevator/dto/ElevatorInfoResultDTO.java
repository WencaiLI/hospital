package com.thtf.elevator.dto;

import com.thtf.common.entity.itemserver.TblItemParameter;
import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/5 11:51
 * @Description:
 */
@Data
public class ElevatorInfoResultDTO {
    private Long itemId; // 设备id
    private String itemCode; // 设备编码
    private String itemName; // 设备名称
    private String areaName; // 设备所在地区
    List<TblItemParameter> parameterList; // 参数列表
}
