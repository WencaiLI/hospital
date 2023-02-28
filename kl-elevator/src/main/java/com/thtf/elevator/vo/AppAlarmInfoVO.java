package com.thtf.elevator.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2023/2/28 19:11
 * @Description:
 */
@Data
public class AppAlarmInfoVO {
    /**
     * 未处理报警数量
     */
    Long alarmUnHandleNum;

    /**
     * 已处理报警数量
     */
    Long alarmHasHandledNum;
}
