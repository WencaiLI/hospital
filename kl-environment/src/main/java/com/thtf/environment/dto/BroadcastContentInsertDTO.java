package com.thtf.environment.dto;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/11/3 15:51
 * @Description:
 */
@Data
public class BroadcastContentInsertDTO {

    private Long id;
    private String itemCode;
    private String broadcastContentId;
    private Object detail;
}
