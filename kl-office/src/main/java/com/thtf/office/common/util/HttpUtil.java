package com.thtf.office.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @Auther: liwencai
 * @Date: 2022/8/2 13:49
 * @Description: Http工具类
 */
@Slf4j
public class HttpUtil {

    /**
     * @Author: liwencai
     * @Description: 获取HttpServletRequest
     * @Date: 2022/8/2
     * @return: javax.servlet.http.HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }
    /**
     * @Author: liwencai
     * @Description: 获取token
     * @Date: 2022/8/2
     * @return: java.lang.String
     */
    public static String getToken(){
        // todo 此处过为空则给定
        String token = getRequest().getHeader("Authorization");
        if(token == null){
            token = "de786585-465b-4215-bc74-607e395554ba";
        }
        return  token;
    }
}
