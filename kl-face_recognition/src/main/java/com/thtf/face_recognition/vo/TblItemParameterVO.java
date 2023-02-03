package com.thtf.face_recognition.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thtf.common.entity.itemserver.TblItemParameter;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * @Author: liwencai
 * @Date: 2023/2/3 10:02
 * @Description: 排除保留字段等敏感信息
 */
@EqualsAndHashCode(callSuper = true)
public class TblItemParameterVO extends TblItemParameter {
    @JsonIgnore
    private LocalDateTime createdTime;
    @JsonIgnore
    private LocalDateTime dataUpdateTime;
    @JsonIgnore
    private LocalDateTime deleteTime;
    @JsonIgnore
    private String createdBy;
    @JsonIgnore
    private String updateBy;
    @JsonIgnore
    private String deleteBy;
    @JsonIgnore
    private String min;
    @JsonIgnore
    private String max;
}
