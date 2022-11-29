package com.thtf.elevator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Author: liwencai
 * @Date: 2022/9/5 12:26
 * @Description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ElevatorAlarmResultDTO extends ElevatorInfoResultDTO{
    private Integer alarmLevel;
    private Integer alarmCategory;
    private String stayTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmTime;
}
