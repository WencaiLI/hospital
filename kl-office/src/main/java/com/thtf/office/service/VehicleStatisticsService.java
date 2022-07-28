package com.thtf.office.service;

import com.thtf.office.vo.VehicleRankingsResultVO;
import com.thtf.office.vo.VehicleStatisticsParamVO;
import com.thtf.office.vo.VehicleStatisticsResultVO;

import java.util.List;
import java.util.Map;

/**
 * @Auther: liwencai
 * @Date: 2022/7/27 23:11
 * @Description:
 */
public interface VehicleStatisticsService {

    List<VehicleStatisticsResultVO> getVehicleStatus();

    List<VehicleStatisticsResultVO> getVehicleCategory(VehicleStatisticsParamVO paramVO);

    List<VehicleRankingsResultVO> getRankings(Map<String, Object> map);

    List<VehicleRankingsResultVO> getMaintenanceRankings(VehicleStatisticsParamVO paramVO);
}
