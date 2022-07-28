package com.thtf.office.service;

import com.thtf.office.vo.VehicleInfoParamVO;
import com.thtf.office.entity.TblVehicleInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.office.vo.VehicleSelectByDateResult;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车辆信息表 服务类
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
public interface TblVehicleInfoService extends IService<TblVehicleInfo> {

    List<TblVehicleInfo> select(VehicleInfoParamVO paramVO);

    boolean deleteById(Long vid);

    boolean insert(TblVehicleInfo paramVO);

    boolean insertBatch(List<TblVehicleInfo> list);

    boolean updateSpec(VehicleInfoParamVO paramVO);

    List<VehicleSelectByDateResult> selectByCidByDate(Map<String, Object> selectByCidByDateMap);
}
