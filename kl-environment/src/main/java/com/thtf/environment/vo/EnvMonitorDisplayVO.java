package com.thtf.environment.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/10/25 14:21
 * @Description:
 */
@Data
public class EnvMonitorDisplayVO {
    private Integer itemNum; // 设备总数
    private Integer itemOnlineNum; // 设备在线数
    private Integer monitorAlarmNum; // 监测报警数量
    private Integer malfunctionAlarmNum; // 故障报警总数
}
