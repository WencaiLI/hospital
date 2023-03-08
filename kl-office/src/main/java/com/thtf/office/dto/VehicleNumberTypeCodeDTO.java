package com.thtf.office.dto;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/11/17 18:46
 * @Description:
 */
@Data
public class VehicleNumberTypeCodeDTO {

    /**
     * 车牌号
     */
    private String carNumber;

    /**
     * 车辆类别名称
     */
    private String typeName;
}
