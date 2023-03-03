package com.thtf.office.service.impl;

import com.thtf.office.dto.VehicleNumberTypeCodeDTO;
import com.thtf.office.mapper.TblVehicleInfoMapper;
import com.thtf.office.mapper.TblVehicleMaintenanceMapper;
import com.thtf.office.mapper.TblVehicleSchedulingMapper;
import com.thtf.office.service.VehicleStatisticsService;
import com.thtf.office.vo.VehicleRankingsResultVO;
import com.thtf.office.vo.VehicleStatisticsParamVO;
import com.thtf.office.vo.VehicleStatisticsResultVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: liwencai
 * @Date: 2022/7/27 23:11
 * @Description:
 */
@Service
public class VehicleStatisticsServiceImpl implements VehicleStatisticsService {
    @Resource
    private TblVehicleInfoMapper vehicleInfoMapper;
    @Resource
    private TblVehicleMaintenanceMapper vehicleMaintenanceMapper;
    @Resource
    private TblVehicleSchedulingMapper vehicleSchedulingMapper;

    /**
     * @Author: liwencai
     * @Description: 车辆调度状态实时统计
     * @Date: 2022/7/28
     * @Param paramVO:
     * @return: java.util.List<com.thtf.office.vo.VehicleStatisticsResultVO>
     */
    @Override
    public List<VehicleStatisticsResultVO> getVehicleStatus(Map<String,Object> map) {
        List<VehicleStatisticsResultVO> result = vehicleInfoMapper.getVehicleStatus(map);
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

    /**
     * @Author: liwencai
     * @Description: 获取公车调度相关排行榜信息：部门用车、司机出车、车辆使用排行榜。
     * @Date: 2022/7/28
     * @Param map:
     * @return: java.util.List<com.thtf.office.vo.VehicleRankingsResultVO>
     */
    @Override
    public List<VehicleRankingsResultVO> getRankings(Map<String, Object> map) {
        List<VehicleRankingsResultVO> rankings = vehicleInfoMapper.getRankings(map);
        if(null !=map.get("isNeedCategory") && (Boolean) map.get("isNeedCategory")){
            List<String> carNumberList = rankings.stream().map(VehicleRankingsResultVO::getAttribute).collect(Collectors.toList());
            List<VehicleNumberTypeCodeDTO> vehicleNumberTypeCodeDTOS = vehicleInfoMapper.listCarTypeCodeList(carNumberList);
            for (VehicleRankingsResultVO vehicleRankingsResultVO : rankings) {
                for (VehicleNumberTypeCodeDTO vehicleNumberTypeCodeDTO : vehicleNumberTypeCodeDTOS) {
                    if(vehicleRankingsResultVO.getAttribute().equals(vehicleNumberTypeCodeDTO.getCarNumber())){
                        vehicleRankingsResultVO.setAttributeTwo(vehicleNumberTypeCodeDTO.getTypeName());
                    }
                }
            }
        }

        return rankings;
    }

    /**
     * @Author: liwencai
     * @Description: 获取维保频次排行
     * @Date: 2022/7/28
     * @Param paramVO:
     * @return: java.util.List<com.thtf.office.vo.VehicleRankingsResultVO>
     */
    @Override
    public List<VehicleRankingsResultVO> getMaintenanceRankings(VehicleStatisticsParamVO paramVO) {
        List<VehicleRankingsResultVO> rankings = vehicleMaintenanceMapper.getMaintenanceRankings(paramVO);
        List<String> carNumberList = rankings.stream().map(VehicleRankingsResultVO::getAttribute).collect(Collectors.toList());
        List<VehicleNumberTypeCodeDTO> vehicleNumberTypeCodeDTOS = vehicleInfoMapper.listCarTypeCodeList(carNumberList);
        for (VehicleRankingsResultVO vehicleRankingsResultVO : rankings) {
            for (VehicleNumberTypeCodeDTO vehicleNumberTypeCodeDTO : vehicleNumberTypeCodeDTOS) {
                if(vehicleRankingsResultVO.getAttribute().equals(vehicleNumberTypeCodeDTO.getCarNumber())){
                    vehicleRankingsResultVO.setAttributeTwo(vehicleNumberTypeCodeDTO.getTypeName());
                }
            }
        }
        return rankings;
    }

    /**
     * @Author: liwencai
     * @Description: 公车调度时长排行榜
     * @Date: 2022/11/2
     * @Param: paramVO:
     * @Return: java.util.List<com.thtf.office.vo.VehicleRankingsResultVO>
     */
    @Override
    public List<VehicleRankingsResultVO> rankingsOfSchWD(VehicleStatisticsParamVO paramVO) {
        List<VehicleRankingsResultVO> rankings = vehicleSchedulingMapper.rankingsOfSchWD(paramVO);
        List<String> carNumberList = rankings.stream().map(VehicleRankingsResultVO::getAttribute).collect(Collectors.toList());
        List<VehicleNumberTypeCodeDTO> vehicleNumberTypeCodeDTOS = vehicleInfoMapper.listCarTypeCodeList(carNumberList);
        for (VehicleRankingsResultVO vehicleRankingsResultVO : rankings) {
            for (VehicleNumberTypeCodeDTO vehicleNumberTypeCodeDTO : vehicleNumberTypeCodeDTOS) {
                if(vehicleRankingsResultVO.getAttribute().equals(vehicleNumberTypeCodeDTO.getCarNumber())){
                    vehicleRankingsResultVO.setAttributeTwo(vehicleNumberTypeCodeDTO.getTypeName());
                }
            }
        }
        return rankings;
    }
}
