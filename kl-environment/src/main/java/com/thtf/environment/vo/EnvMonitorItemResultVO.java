package com.thtf.environment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.environment.dto.VideoInfoDTO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/25 14:55
 * @Description:
 */
@Data
public class EnvMonitorItemResultVO {
    /**
     * 设备编码
     */
    private String itemCode;

    /**
     * 设备名称
     */
    private String itemName;

    /**
     * 设备类别编码
     */
    private String itemTypeCode;

    /**
     * 设备类别名称
     */
    private String itemTypeName;

    /**
     * 建筑编码
     */
    private String buildingCode;

    /**
     * 建筑名称
     */
    private String buildingName;

    /**
     * 区域编程
     */
    private String areaCode;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 所在分组id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    /**
     * 所在分组名称
     */
    private String groupName;

    /**
     * 在线状态参数编码
     */
    private String onlineParameterCode;

    /**
     * 在线状态
     */
    private String onlineParameterValue;

    /**
     * 在线状态参数编码
     */
    private String alarmParameterCode;

    /**
     * 在线状态参数编码
     */
    private String alarmParameterValue;

    /**
     * 在线状态参数编码
     */
    private String faultParameterCode;

    /**
     * 故障参数值
     */
    private String faultParameterValue;

    /**
     * 报警状态
     */
    private Integer alarmCategory;

    /**
     * 数据采集值
     */
    private Object dataCollectionValue;

    /**
     * 数据采集时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataCollectionTime;

    /**
     * 模型视角
     */
    private List<Integer> eye;

    /**
     * 模型视角
     */
    private List<Integer> center;

}
