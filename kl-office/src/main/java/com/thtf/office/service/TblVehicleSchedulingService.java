package com.thtf.office.service;

import com.thtf.office.dto.TblUserScheduleDTO;
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

    /**
     * @Description 查询未调度司机列表
     * @param positionTitle
     * @return
     * @author guola
     * @Date 2022-07-28
     */
    List<TblUserScheduleDTO> findDriverForSchedule(String positionTitle);
}
