package com.thtf.face_recognition.application.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/11/7 10:15
 * @Description:
 */
@Data
public class FaceRecognitionItemParamVO {

    /**
     * 关键词
     */
    private String sysCode;

    /**
     * 建筑编码（逗号分隔）
     */
    private String buildingCodes;

    /**
     * 建筑区域编码
     */
    private String areaCodes;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 页号
     */
    private Integer pageNumber;

    /**
     * 页大小
     */
    private Integer pageSize;
}
