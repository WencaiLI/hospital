package com.thtf.environment.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/9/22 21:59
 * @Description: 大屏发布内容 （具体需要结合高博医院的具体接口）
 */
@Data
public class PublishContent {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String title;
    private String content;
}
