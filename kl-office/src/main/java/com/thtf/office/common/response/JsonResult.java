package com.thtf.office.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接口响应包装类
 *
 * @author zhaoshengyan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JsonResult<T>{

    /**
     * 接口状态码
     */
    private Integer code;

    /**
     * 接口状态(success/error)
     */
    private String status;

    /**
     * 接口响应数据
     */
    private T data;

    /**
     * 接口响应描述
     */
    private String description;

    public static <T> JsonResult<T> success(T data) {
        return new JsonResult<>(200, "success", data, "请求成功");
    }

    public static <T> JsonResult<String> error(Integer code, String data) {
        return new JsonResult<>(code, "error", data, "description");
    }

    public static <T> JsonResult<String> error(Integer code, String data, String description) {
        return new JsonResult<>(code, "error", data, description);
    }

    public static <T> JsonResult<T> error(String description) {
        return new JsonResult<>(500, "error", null, description);
    }
}
