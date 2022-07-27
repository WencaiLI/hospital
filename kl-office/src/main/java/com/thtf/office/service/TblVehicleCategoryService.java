package com.thtf.office.service;

import com.thtf.office.vo.VehicleCategoryParamVO;
import com.thtf.office.entity.TblVehicleCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 车辆类别表 服务类
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
public interface TblVehicleCategoryService extends IService<TblVehicleCategory> {

    /**
     * @Author: liwencai
     * @Description: 新增公车类别
     * @Date: 2022/7/27
     * @Param vehicleCategoryParamVO:
     * @return: java.lang.Boolean
     */
    Boolean insert(VehicleCategoryParamVO vehicleCategoryParamVO);

    /**
     * @Author: liwencai
     * @Description: 综合查询公车类别
     * @Date: 2022/7/26
     * @Param vehicleCategoryParamVO:
     * @return: java.awt.List
     */
    List<TblVehicleCategory> select(VehicleCategoryParamVO vehicleCategoryParamVO);

    boolean deleteById(Long cid);


    boolean updateSpec(VehicleCategoryParamVO vehicleCategoryParamVO);
}
