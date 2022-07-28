package com.thtf.office.service;

import com.thtf.office.vo.VehicleStatisticsParamVO;
import com.thtf.office.vo.VehicleStatisticsResultVO;

import java.util.List;

/**
 * @Auther: liwencai
 * @Date: 2022/7/27 23:11
 * @Description:
 */
public interface VehicleStatisticsService {
    List<VehicleStatisticsResultVO> getVehicleStatus(VehicleStatisticsParamVO paramVO);

    List<VehicleStatisticsResultVO> getVehicleCategory(VehicleStatisticsParamVO paramVO);
}
