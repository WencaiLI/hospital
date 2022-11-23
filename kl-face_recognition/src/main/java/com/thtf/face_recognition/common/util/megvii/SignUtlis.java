package com.thtf.face_recognition.common.util.megvii;

/**
 * @Author: liwencai
 * @Date: 2022/11/7 13:32
 * @Description:
 */

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 描䘠：
 Author：liuxing
 Date：2020-09-11
 */
public class SignUtlis {
    private static final String SIGN_FORMAT = "%s-%s-%s-%s-%s-%s-%s-%s";
    /**
     签名算法生成卡密昂
     @param ctimestamp
     @param cnonce
     @param requestBody 请求体对象
     @param requestParam
     @param url
     @param method
     @param cappKey
     @param secret
     @return
     */
    public static String sign(String ctimestamp, String cnonce, Object requestBody, String requestParam,String url,String method,String cappKey,String secret) {
        String requestBodyMd5 = "";
        if (requestBody != null) {
            String requestBodyJson = JSONObject.toJSONString(requestBody);
            if (StringUtils.isNotEmpty(requestBodyJson)) {
                requestBodyMd5 = DigestUtil.encryptMd5(requestBodyJson);
            }
        }
        String signStr = String.format(SIGN_FORMAT, url, method, requestParam, requestBodyMd5, cappKey, ctimestamp, cnonce ,secret);
        return DigestUtil.encryptMd5(signStr);
    }
}
