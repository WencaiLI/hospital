package com.thtf.environment.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/7 12:28
 * @Description:
 */
@Data
public class DisplayInfoDTO {

    private Integer areaNum; // 区域总数

    private Integer runningAreaNum; // 运行广播区域总数

    private Integer itemNum; // 设备总数

    private Integer runningItemNum; // 运行设备总数

    private Integer faultItemNum; // 故障报警

   //  List<KeyValueDTO> results;
}
