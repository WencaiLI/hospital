package com.thtf.office.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.vo.VehicleSchedulingParamVO;
import com.thtf.office.vo.VehicleSelectByDateResult;

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

    boolean insert(VehicleSchedulingParamVO paramVO);

    boolean updateSpec(VehicleSchedulingParamVO paramVO);

    List<VehicleSelectByDateResult> selectInfoAboutDri();

    /**
     * @Description 生成最新的调度流水号
     * @param
     * @return  调度流水号字符串
     * @author guola
     * @date 2022-07-28
     */
    String createSerialNumber();
}
