package com.thtf.office.mapper;

import com.thtf.office.vo.VehicleMaintenanceParamVO;
import com.thtf.office.entity.TblVehicleMaintenance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thtf.office.vo.VehicleRankingsResultVO;
import com.thtf.office.vo.VehicleStatisticsParamVO;

import java.util.List;

/**
 * <p>
 * 车辆维保表 Mapper 接口
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
public interface TblVehicleMaintenanceMapper extends BaseMapper<TblVehicleMaintenance> {

    List<TblVehicleMaintenance> select(VehicleMaintenanceParamVO vehicleMaintenanceParamVO);

    List<VehicleRankingsResultVO> getMaintenanceRankings(VehicleStatisticsParamVO paramVO);
}
