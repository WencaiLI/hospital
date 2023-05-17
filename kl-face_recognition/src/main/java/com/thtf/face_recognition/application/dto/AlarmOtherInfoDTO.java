package com.thtf.face_recognition.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liwencai
 * @since 2023/5/16
 */
@Getter
@Setter
@Builder
public class AlarmOtherInfoDTO {
    /**
     * 报警点位参数
     */
    String parameterCode;
    /**
     * 子系统编码
     */
    String systemCode;
    /**
     * 子系统名称
     */
    String systemName;

    /**
     * 报警等级 默认2
     */
    Integer alarmLevel = 2;

    /**
     * 报警类型 默认1
     */
    Integer alarmType = 1;

    /**
     * 报警类别 默认报警
     */
    Integer alarmCategory = 0;
}
