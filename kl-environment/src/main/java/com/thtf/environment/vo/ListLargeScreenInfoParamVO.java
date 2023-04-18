package com.thtf.environment.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;


/**
 * @Author: liwencai
 * @Date: 2023/3/15 18:41
 * @Description:
 */
@Data
@Builder
public class ListLargeScreenInfoParamVO {
    private String sysCode;
    private List<String> buildingCodeList;
    private List<String> areaCodeList;
    private String onlineValue;
    private String keyword;
    private Integer pageNumber;
    private Integer pageSize;
}
