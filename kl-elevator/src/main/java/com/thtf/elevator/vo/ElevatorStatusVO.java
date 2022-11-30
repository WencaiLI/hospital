package com.thtf.elevator.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/11/29 09:41
 * @Description: 电梯点位相关信息
 */
@Data
public class ElevatorStatusVO {

    /**
     * 运行状态当前值
     */
    private String runParameterValue;

    /**
     * 上行状态
     */
    private String upGoingParameterValue;

    /**
     * 下行状态
     */
    private String downGoingParameterValue;

    /**
     * 楼层当前值
     */
    private String currentFloorValue;

    /**
     * 运行时长值
     */
    private String runTimeParameterValue;

    /**
     * 锁梯状态值
     */
    private String lockStatusParameterValue;

    /**
     * 故障状态值
     */
    private String faultParameterValue;

    /**
     * 故障状态值
     */
    private String alarmParameterValue;

    /**
     * 超载参数值
     */
    private String overLoadParameterValue;
}
