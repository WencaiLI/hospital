package com.thtf.environment.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/10/25 18:19
 * @Description:
 */
@Data
public class ItemParameterInfoVO {
    /**
     * 名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 参数类别
     */
    private String parameterType;
}
