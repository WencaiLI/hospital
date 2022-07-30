package com.thtf.office.mapper;

import com.thtf.office.vo.VehicleCategoryParamVO;
import com.thtf.office.entity.TblVehicleCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * <p>
 * 车辆类别表 Mapper 接口
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
@Component
public interface TblVehicleCategoryMapper extends BaseMapper<TblVehicleCategory> {

    List<TblVehicleCategory> select(VehicleCategoryParamVO vehicleCategoryParamVO);

}
