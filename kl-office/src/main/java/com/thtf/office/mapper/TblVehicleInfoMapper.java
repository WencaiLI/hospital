package com.thtf.office.mapper;

import com.thtf.office.vo.VehicleInfoParamVO;
import com.thtf.office.entity.TblVehicleInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 车辆信息表 Mapper 接口
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
public interface TblVehicleInfoMapper extends BaseMapper<TblVehicleInfo> {

    List<TblVehicleInfo> select(VehicleInfoParamVO paramVO);

    Integer setCidToNull(@Param("cid") Long cid);
}
