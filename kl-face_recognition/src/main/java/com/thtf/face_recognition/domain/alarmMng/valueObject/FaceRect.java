package com.thtf.face_recognition.domain.alarmMng.valueObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2023/1/7 19:42
 * @Description:
 */
@Data
public class FaceRect {
    /**
     * 目标在图片中的距离，左
     */
    private Float left;

    /**
     * 目标在图片中的距离，上
     */
    private Float top;

    /**
     * 目标在图片中的距离，右
     */
    private Float right;
    /**
     * 目标在图片中的距离，下
     */
    private Float bottom;


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
    public static FaceRect convertToFaceRect(String jsonString) {
        if(StringUtils.isBlank(jsonString)){
            return null;
        }
        return JSONObject.parseObject(jsonString,FaceRect.class);
    }


    public static List<FaceRect> convertToFaceRectList(String jsonString) {
        if(StringUtils.isBlank(jsonString)){
            return null;
        }
        return JSON.parseArray(jsonString,FaceRect.class);
    }
}
