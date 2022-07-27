package com.thtf.office.mapper;

import com.thtf.office.vo.VehicleSchedulingParamVO;
import com.thtf.office.entity.TblVehicleScheduling;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

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
}
