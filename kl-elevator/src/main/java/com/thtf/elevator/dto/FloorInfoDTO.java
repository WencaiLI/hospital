package com.thtf.elevator.dto;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/10/8 09:20
 * @Description: 楼层信息dto
 */
@Data
public class FloorInfoDTO {
    private Long id; // 楼层所在id
    private String name; // 楼层名称
    private String code; // 楼层编码
}
