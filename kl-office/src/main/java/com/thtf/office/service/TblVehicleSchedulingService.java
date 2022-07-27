package com.thtf.office.service;

import com.thtf.office.vo.VehicleSchedulingParamVO;
import com.thtf.office.entity.TblVehicleScheduling;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 车辆调度表 服务类
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
public interface TblVehicleSchedulingService extends IService<TblVehicleScheduling> {

    boolean deleteById(Long sid);

    List<TblVehicleScheduling> select(VehicleSchedulingParamVO paramVO);
}
