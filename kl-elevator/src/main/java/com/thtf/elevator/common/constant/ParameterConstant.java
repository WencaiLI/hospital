package com.thtf.elevator.common.constant;

/**
 * @Author: liwencai
 * @Date: 2022/11/29 09:55
 * @Description:
 */
public class ParameterConstant {
    /* 电梯子系统展示参数 */

    /**
     * 超载
     */
    public static final String ELEVATOR_OVERLOAD = "OverLoad";


    /**
     * 运行状态
     */
    public static final String ELEVATOR_RUN_STATUS = "State";

    /**
     * 电梯楼层
     */
    public static final String ELEVATOR_CURRENT_FLOOR = "currentFloor";

    /**
     * 电梯上行状态参数类别编码
     */
    public static final String ELEVATOR_UP_GOING_STATUS = "isUpGoing";

    /**
     * 电梯下行状态参数类别编码
     */
    public static final String ELEVATOR_DOWN_GOING_STATUS = "isDownGoing";

    /**
     * 电梯运行时长参数类别编码
     */
    public static final String ELEVATOR_RUN_TIME = "accRunTime";

    /**
     * 锁梯状态
     */
    public static final String ELEVATOR_LOCK_STATUS = "lockStatus";

    /**
     * 报警状态
     */
    public static final String ELEVATOR_ALARM = "Alarm";
    /**
     * 扶梯报警状态
     */
    public static final String ELEVATOR_F_ALARM = "LiftFaultAlarm";

    /**
     * 直梯报警状态
     */
    public static final String ELEVATOR_Z_ALARM = "faultStatus";

    /**
     * 故障状态
     */
    public static final String ELEVATOR_FAULT = "Fault";

    /**
     * 直梯运行状态编码
     */
    public static final String ELEVATOR_Z_RUN_STATUS = "PowerStatus";

    /**
     * 扶梯运行状态编码
     */
    public static final String ELEVATOR_F_RUN_STATUS = "FaultStatus";
}
