package com.thtf.office.dto;

import com.thtf.office.vo.VehicleStatisticsResultVO;
import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/8/2 09:57
 * @Description:
 */
@Data
public class SelectAllInfoResultDTO {
    private String categoryName;
    private List<VehicleStatisticsResultVO> data;
    private Integer totalNumber;
}
