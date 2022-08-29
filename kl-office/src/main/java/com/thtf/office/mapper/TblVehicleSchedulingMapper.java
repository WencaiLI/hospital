package com.thtf.office.mapper;

import com.thtf.office.vo.VehicleRankingsResultVO;
import com.thtf.office.vo.VehicleSchedulingParamVO;
import com.thtf.office.entity.TblVehicleScheduling;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thtf.office.vo.VehicleSelectByDateResult;
import com.thtf.office.vo.VehicleStatisticsParamVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车辆调度表 Mapper 接口
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
public interface TblVehicleSchedulingMapper extends BaseMapper<TblVehicleScheduling> {

    List<TblVehicleScheduling> select(VehicleSchedulingParamVO paramVO);

    List<VehicleSelectByDateResult> selectScheAboutDir(Map<String, Object> monthNumber);

    List<VehicleRankingsResultVO> rankingsOfSchWD(VehicleStatisticsParamVO paramVO);
}
