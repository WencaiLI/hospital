package com.thtf.face_recognition.dto;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/12/7 09:47
 * @Description:
 */
@Data
public class MegviiListDeviceParamDTO {
    /**
     * 设备名称
     */
    private String name;

    /**
     * 页号
     */
    private Integer pageNum;

    /**
     * 页大小
     */
    private Integer pageSize;

    /**
     * 设备类型
     */
    private Integer deviceType;
}
