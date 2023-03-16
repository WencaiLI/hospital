package com.thtf.environment.vo;

import lombok.Builder;
import lombok.Data;


/**
 * @Author: liwencai
 * @Date: 2023/3/15 18:41
 * @Description:
 */
@Data
@Builder
public class ListLargeScreenInfoParamVO {
    private String sysCode;
    private String buildingCodes;
    private String areaCodes;
    private String onlineValue;
    private String keyword;
    private Integer pageNumber;
    private Integer pageSize;
}
