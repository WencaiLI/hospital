package com.thtf.elevator.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @Author: liwencai
 * @Date: 2023/3/15 09:19
 * @Description:
 */
@RefreshScope
@Getter
@Component
public class ItemParameterConfig {
    /**
     * 开关状态
     */
    @Value("${STATE}")
    private String state;

    /**
     * 报警状态
     */
    @Value("${ALARM}")
    private String alarm;

    /**
     * 故障状态
     */
    @Value("${FAULT}")
    private String fault;

    /** ***************************** 子系统专属设备参数编码 ***************************** **/
    /**
     * 超载
     */
    @Value("${elevator-system.overLoad}")
    private String overLoad;

    /**
     * 当前楼层
     */
    @Value("${elevator-system.currentFloor}")
    private String currentFloor;

    /**
     * 上行
     */
    @Value("${elevator-system.upGoing}")
    private String upGoing;

    /**
     * 下行
     */
    @Value("${elevator-system.downGoing}")
    private String downGoing;

    /**
     * 运行时长
     */
    @Value("${elevator-system.accRunTime}")
    private String runTime;

    /**
     * 锁梯状态
     */
    @Value("${elevator-system.lockStatus}")
    private String lockStatus;
}
