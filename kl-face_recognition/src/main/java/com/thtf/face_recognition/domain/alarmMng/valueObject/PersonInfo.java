package com.thtf.face_recognition.domain.alarmMng.valueObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liwencai
 * @since 2023/5/16
 */
public class PersonInfo {

    private String uuid;
    /**
     * 姓名
     */
    private String name;
    /**
     * 省份证号
     */
    private String identityCardNum;
    /**
     * 电话号码
     */
    private String phone;

    /**
     * 身体图片
     */
    private String bodyImageUrl;

    /**
     * 图片的三位定位
     */
    private FaceRect faceRect;


    /**
     * 转换为Json字符串
     * @author liwencai
     * @return {@link String}
     */
    public String toJsonString(){
        return JSON.toJSONString(this);
    }

    /**
     * 将String字符串转换为PersonInfo对象
     * @author liwencai
     * @param jsonString PersonInfo的JSON字符串
     * @return {@link PersonInfo}
     */
    public static PersonInfo convertToPersonInfo(String jsonString) {
        if(StringUtils.isBlank(jsonString)){
            return null;
        }
        return JSONObject.parseObject(jsonString,PersonInfo.class);
    }

}
