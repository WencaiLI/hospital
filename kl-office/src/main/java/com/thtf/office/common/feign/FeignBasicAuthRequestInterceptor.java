package com.thtf.office.common.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @Author: liwencai
 * @Date: 2022/9/19 16:38
 * @Description: feign 拦截器
 */
@Slf4j
public class FeignBasicAuthRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //获取到进入时的request对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (null != attributes) {
            HttpServletRequest request = attributes.getRequest();
            //获取到请求头名字
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                //获取到请求头
                String value = request.getHeader(name);
                //经过处理逻辑后
                //使用进入的request对象来设置Feign请求对象的请求头
                requestTemplate.header(name, value);
            }
        }
    }
}