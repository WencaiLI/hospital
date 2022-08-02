package com.thtf.office.common.util;

import com.thtf.office.common.dto.adminserver.UserInfo;
import com.thtf.office.feign.AdminAPI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

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
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
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
