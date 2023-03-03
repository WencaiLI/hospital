package com.thtf.office.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.vo.VehicleSchedulingParamVO;
import com.thtf.office.vo.VehicleSchedulingQueryVO;
import com.thtf.office.vo.VehicleSelectByDateResult;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车辆调度表 服务类
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
public interface TblVehicleSchedulingService extends IService<TblVehicleScheduling> {

    /**
     * @Author: liwencai
     * @Description: 删除调度信息
     * @Date: 2022-07-28
     * @Param sid:
     * @Return: boolean
     */
    boolean deleteById(Long sid);

    /**
     * @Author: liwencai
     * @Description: 筛选调度信息
     * @Date: 2022-07-28
     * @Param paramVO:
     * @Return: java.util.List<com.thtf.office.vo.VehicleSchedulingQueryVO>
     */
    List<VehicleSchedulingQueryVO> select(VehicleSchedulingParamVO paramVO);

    /**
     * @Author: liwencai
     * @Description: 新增调度信息
     * @Date: 2022-07-28
     * @Param paramVO:
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String, Object> insert(VehicleSchedulingParamVO paramVO);

    /**
     * @Author: liwencai
     * @Description: 更新调度信息
     * @Date: 2022-07-28
     * @Param paramVO:
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String, Object> updateSpec(VehicleSchedulingParamVO paramVO);

    /**
     * @Author: liwencai
     * @Description: 查询司机的调度情况
     * @Date: 2022-07-28
     * @Return: java.util.List<com.thtf.office.vo.VehicleSelectByDateResult>
     */
    List<VehicleSelectByDateResult> selectInfoAboutDri();

    /**
     * @Description 生成最新的调度流水号
     * @param
     * @return  调度流水号字符串
     * @author guola
     * @date 2022-07-28
     */
    String createSerialNumber();

    PageInfo<VehicleSchedulingQueryVO> selectPage(VehicleSchedulingParamVO paramVO);
}
