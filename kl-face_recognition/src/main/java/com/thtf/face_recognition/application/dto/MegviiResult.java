package com.thtf.face_recognition.application.dto;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/12/6 22:50
 * @Description:
 */
@Data
public class MegviiResult {

    /**
     * 编码
     */
    private Integer code;

    /**
     * 数据
     */
    private Object data;

    /**
     * 信息
     */
    private String msg;
}
