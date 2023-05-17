package com.thtf.face_recognition.application.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/12/6 11:14
 * @Description: 公用传参参数
 */
@Data
public class BuildingAreaParamVO {
    /**
     * 子系统编码
     */
    private String sysCode;
    /**
     * 建筑编码集
     */
    private String buildingCodes;

    /**
     * 区域编码
     */
    private String areaCode;
}
