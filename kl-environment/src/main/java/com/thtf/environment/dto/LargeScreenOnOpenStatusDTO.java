package com.thtf.environment.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/9/22 22:06
 * @Description: 处于开启状态的大屏DTO
 */
@Data
public class LargeScreenOnOpenStatusDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long itemId; // 设备id

    private String itemCode; // 设备编码

    private String itemName; // 设备名称

    private String areaCode; // 区域编码

    private String areaName; // 区域名称

    private Long playDuration; // 播放时长（/秒）（暂定设备开启时长）

}
