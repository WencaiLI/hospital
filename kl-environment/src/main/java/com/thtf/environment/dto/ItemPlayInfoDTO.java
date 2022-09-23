package com.thtf.environment.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/22 22:16
 * @Description: 设备关联播单信息
 */
@Data
public class ItemPlayInfoDTO {
    private Long itemId; // 设备id

    private String itemCode; // 设备编码

    private List<Playlist> playlist; // 播单信息列表

    private String screenType; // 屏幕类型（横屏竖屏）（最好使用枚举类，枚举状态）

    private String playRule; // 播放规则 （单时段）（最好使用枚举类，枚举状态）

    private String repeatProjection; // 重复计划（持续）（最好使用枚举类，枚举状态）

    private LocalDate startDate; // 播放开始日期

    private LocalDate endDate; // 播放结束日期

    private LocalTime startTime; // 播放开始时间

    private LocalTime endTime; // 播放结束时间

}
