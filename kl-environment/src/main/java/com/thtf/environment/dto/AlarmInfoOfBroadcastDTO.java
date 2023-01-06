package com.thtf.environment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Author: liwencai
 * @Date: 2022/10/7 22:37
 * @Description:
 */
@Data
public class AlarmInfoOfBroadcastDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long itemId; // 设备id

    private String itemName; // 设备名称

    private String itemCode; // 设备编码

    private String buildingCode; // 建筑编码

    private String areaCode; // 区域编码

    private String areaName; // 区域名称

    private String ipAddress; // ip地址

    @JsonSerialize(using = ToStringSerializer.class)
    private Long alarmId; // 报警id

    private Integer alarmLevel; // 报警级别

    private Integer alarmCategory; // 报警类别

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmTime; // 数据报送时间

    private String stayTime; // 滞留时长
}
