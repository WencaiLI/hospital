package com.thtf.office.common.annotation;

import java.lang.annotation.*;

/**
 * @Auther: liwencai
 * @Date: 2022/8/2 09:22
 * @Description: 自定义操作日志
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OperationLog {
    String event() default "";
    RequestWay requestWay();
    String visitPage() default "";
}
