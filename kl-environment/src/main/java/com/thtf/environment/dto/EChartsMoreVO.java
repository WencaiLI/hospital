package com.thtf.environment.dto;

import lombok.Data;
import java.util.List;


/**
 * @Author: liwencai
 * @Date: 2023/1/11 19:20
 * @Description: 多折线图
 */
@Data
public class EChartsMoreVO {
    private List<Integer> keys;
    private List<KeyValueDTO> values;
    // private Map<String, String> codeNameMap;
}
