package com.thtf.elevator.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/8 10:55
 * @Description:
 */
@Data
public class ItemFaultStatisticsDTO {
    private List<String> itemName;
    private List<Integer> monitorAlarmNumber;
    private List<Integer> malfunctionAlarmNumber;
}
