package com.thtf.face_recognition.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/12/7 22:54
 * @Description:
 */
@Data
public class MegviiPersonInfo {

    /**
     * 人员uuid
     */
    private String uuid;

    /**
     * 姓名
     */
    private String name;

    /**
     * 人体图
     */
    private String bodyImageUrl;

    /**
     * 人脸坐标
     */
    private String faceRect;

    /**
     * 目标在图片中的距离，左
     */
    private String left;

    /**
     * 目标在图片中的距离，上
     */
    private String top;

    /**
     * 目标在图片中的距离，右
     */
    private String right;
    /**
     * 目标在图片中的距离，下
     */
    private String bottom;
}
