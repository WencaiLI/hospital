package com.thtf.face_recognition.domain.alarmMng.alarm;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 与ibs5的报警表一致
 * @author liwencai
 * @since 2023/5/16
 */
public class AlarmDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime alarmTime;

    @JsonSerialize(using = ToStringSerializer.class)
    private String itemId;

    private String itemName;

    private String itemCode;

    private String itemTypeCode;

    private String parameterCode;

    private String systemCode;

    private String systemName;

    private String buildingAreaCode;

    private String buildingAreaName;

    private String areaCodes;

    private String buildingArea;

    private String alarmDescription;

    private Integer alarmLevel;

    private Integer alarmType;

    private Integer alarmCategory;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long alarmPlanId;

    private String viewLatitude;

    private String viewLongitude;
}
