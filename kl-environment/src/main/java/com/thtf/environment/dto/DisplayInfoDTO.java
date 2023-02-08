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

    /**
     * 区域总数
     */
    private Integer areaNum;


    /**
     * 运行广播区域总数
     */
    private Integer runningAreaNum;

    /**
     * 设备总数
     */
    private Integer itemNum;

    /**
     * 运行设备总数
     */
    private Integer runningItemNum;

    /**
     * 故障报警
     */
    private Integer faultItemNum;

    /**
     * 报警数量
     */
    private Integer monitorNum;

   //  List<KeyValueDTO> results;
}
