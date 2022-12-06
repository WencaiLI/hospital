package com.thtf.face_recognition.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/11/7 10:49
 * @Description:
 */
@Data
public class FaceRecognitionAlarmParamVO {

    /**
     * 子系统编码
     */
    private String sysCode;

    /**
     * 建筑区域编码集
     */
    private String buildingCodes;

    /**
     * 区域编码
     */
    private String areaCodes;

    /**
     * keyword
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
