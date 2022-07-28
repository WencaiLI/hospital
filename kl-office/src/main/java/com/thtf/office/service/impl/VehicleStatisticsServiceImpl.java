package com.thtf.office.service.impl;

import com.thtf.office.mapper.TblVehicleInfoMapper;
import com.thtf.office.service.VehicleStatisticsService;
import com.thtf.office.vo.VehicleStatisticsParamVO;
import com.thtf.office.vo.VehicleStatisticsResultVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Auther: liwencai
 * @Date: 2022/7/27 23:11
 * @Description:
 */
@Service
public class VehicleStatisticsServiceImpl implements VehicleStatisticsService {
    @Resource
    TblVehicleInfoMapper vehicleInfoMapper;

    /**
     * @Author: liwencai
     * @Description: 车辆调度状态实时统计
     * @Date: 2022/7/28
     * @Param paramVO:
     * @return: java.util.List<com.thtf.office.vo.VehicleStatisticsResultVO>
     */
    @Override
    public List<VehicleStatisticsResultVO> getVehicleStatus(VehicleStatisticsParamVO paramVO) {
        List<VehicleStatisticsResultVO> result = vehicleInfoMapper.getVehicleStatus(paramVO);
        ArrayList<String> attributes = result.stream().map(VehicleStatisticsResultVO::getAttribute).collect(Collectors.toCollection(ArrayList::new));
        /* 数据库中不存在的状态设置为空 */
        Stream.of("待命中","出车中","维修中").forEach(e->{
            if(!attributes.contains(e)){
                VehicleStatisticsResultVO vehicleStatisticsResultVO = new VehicleStatisticsResultVO();
                vehicleStatisticsResultVO.setAttribute(e);
                vehicleStatisticsResultVO.setNumber(0L);
                result.add(vehicleStatisticsResultVO);
            }
        });
        return result;
    }

    /**
     * @Author: liwencai
     * @Description: 各类车辆出车统计
     * @Date: 2022/7/28
     * @Param paramVO:
     * @return: java.util.List<com.thtf.office.vo.VehicleStatisticsResultVO>
     */
    @Override
    public List<VehicleStatisticsResultVO> getVehicleCategory(VehicleStatisticsParamVO paramVO) {

        return vehicleInfoMapper.getVehicleCategory(paramVO);
    }
}
