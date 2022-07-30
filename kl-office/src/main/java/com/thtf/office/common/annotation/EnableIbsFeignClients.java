package com.thtf.office.common.annotation;

import org.springframework.cloud.openfeign.EnableFeignClients;

import java.lang.annotation.*;

/**
 * 自定义 @EnableFeignClients 注解
 *
 * @author ligh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@EnableFeignClients
public @interface EnableIbsFeignClients {

    String[] value() default {};

    String[] basePackages() default {"com.thtf.common"};

    Class<?>[] basePackageClasses() default {};

    Class<?>[] defaultConfiguration() default {};

    Class<?>[] clients() default {};
}
