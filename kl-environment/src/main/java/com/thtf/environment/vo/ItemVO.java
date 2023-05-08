package com.thtf.environment.vo;

import com.thtf.common.dto.itemserver.TblItemDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: liwencai
 * @Date: 2023/5/8 10:40
 * @Description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ItemVO extends TblItemDTO {

    /**
     * 参数单位
     */
    private String unit;
}
