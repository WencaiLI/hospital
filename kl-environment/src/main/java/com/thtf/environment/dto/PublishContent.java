package com.thtf.environment.dto;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/9/22 21:59
 * @Description: 大屏发布内容 （具体需要结合高博医院的具体接口）
 */
@Data
public class PublishContent {
    private Long id;
    private String title;
    private String content;
}
