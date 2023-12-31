package com.thtf.environment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/22 21:44
 * @Description: 大屏故障信息
 */
@Data
public class AlarmInfoOfLargeScreenDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long itemId; // 设备id

    private String itemName; // 设备名称

    private String itemCode; // 设备编码

    private String areaCode; // 区域编码

    private String areaName; // 区域名称

    @JsonSerialize(using = ToStringSerializer.class)
    private Long alarmId; // 报警id

    private Integer alarmLevel; // 报警级别

    private Integer alarmCategory; // 报警类别

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmTime; // 数据报送时间

    private String stayTime; // 滞留时长

    private List<Integer> eye; // 滞留时长

    private List<Integer> center; // 滞留时长

    /* 大屏发布内容 */
    private PublishContent publishContent;
}
