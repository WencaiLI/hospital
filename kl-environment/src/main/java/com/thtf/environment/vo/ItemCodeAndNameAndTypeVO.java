package com.thtf.environment.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/11/23 18:56
 * @Description:
 */
@Data
public class ItemCodeAndNameAndTypeVO {
    /**
     * 设备编码
     */
    private String itemCode;

    /**
     * 设备名称
     */
    private String itemName;

    /**
     * 设备类别编码
     */
    private String itemTypeCode;

    /**
     * 设备类别名称
     */
    private String itemTypeName;
}
