package com.thtf.face_recognition.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2023/1/7 19:42
 * @Description:
 */
@Data
public class FaceRectDTO {
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
}
