package com.thtf.face_recognition.common.auth;

import com.thtf.face_recognition.common.util.megvii.SignUtlis;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liwencai
 * @Date: 2023/2/21 10:37
 * @Description: 旷世请求认证
 */
public class MegviiAuth {
    // appkey 盘古分配
    private static final String cappkey = "";
    // 密钥 盘古分配
    private static final String secret = "";

    private static final String cnonce = "thtfzhly";

    /**
     * @Author: liwencai
     * @Description: 设置认证请求头
     * @Date: 2023/2/21
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     */
    public static Map<String, Object> getAuthHeaders(String requestUrl, String requestMethod, String requestParam, Object requestBody){
        long ctimestamp = System.currentTimeMillis();
        // 应盘古文档要求，为null则设置为“”
        if(StringUtils.isBlank(requestParam)){
            requestParam = "";
        }
        // 应盘古文档要求，为null则设置为“”
        if(null == requestBody){
            requestBody = "";
        }
        String csign = SignUtlis.sign(String.valueOf(ctimestamp), cnonce, requestBody, requestParam, requestUrl, requestMethod, cappkey, secret);
        Map<String, Object> resultMap = new HashMap<>();
        // 客户端本地unix毫秒时间戳
        resultMap.put("ctimestamp",ctimestamp);
        // 6位随机字符串，可含字母，客户端自行生成
        resultMap.put("cnonce",cnonce);
        // 标识第三方系统身份，由盘古分配
        resultMap.put("cappkey",cappkey);
        // 按签名规则计算的签名
        resultMap.put("csign",csign);
        // 区域UUID（选填）
        resultMap.put("zoneUuid","");
        return resultMap;
    }
}
