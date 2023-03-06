package com.thtf.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.security.SecurityContextHolder;
import com.thtf.common.util.IdGeneratorSnowflake;
import com.thtf.office.dto.converter.VehicleMaintenanceConverter;
import com.thtf.office.entity.TblVehicleMaintenance;
import com.thtf.office.mapper.TblVehicleMaintenanceMapper;
import com.thtf.office.service.TblVehicleMaintenanceService;
import com.thtf.office.vo.VehicleMaintenanceParamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 车辆维保表 服务实现类
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
@Service
@Slf4j
public class TblVehicleMaintenanceServiceImpl extends ServiceImpl<TblVehicleMaintenanceMapper, TblVehicleMaintenance> implements TblVehicleMaintenanceService {

    @Resource
    private TblVehicleMaintenanceMapper vehicleMaintenanceMapper;
    
    @Resource
    private VehicleMaintenanceConverter vehicleMaintenanceConverter;

    @Autowired
    private IdGeneratorSnowflake idGeneratorSnowflake;

    /**
     * @Author: liwencai 
     * @Description: 新增维保信息
     * @Date: 2022/8/2
     * @Param vehicleMaintenanceParamVO: 
     * @return: java.lang.Boolean 
     */
    @Override
    public Boolean insert(VehicleMaintenanceParamVO vehicleMaintenanceParamVO) {
        TblVehicleMaintenance maintenance = vehicleMaintenanceConverter.toVehicleMaintenance(vehicleMaintenanceParamVO);
        maintenance.setId(idGeneratorSnowflake.snowflakeId());
        maintenance.setCreateTime(LocalDateTime.now());
        maintenance.setCreateBy( SecurityContextHolder.getUserName());
        return vehicleMaintenanceMapper.insert(maintenance) == 1;
    }

    /**
     * @Author: liwencai
     * @Description: 根据维保id删除维保信息
     * @Date: 2022/7/26
     * @Param mid:
     * @return: boolean
     */
    @Override
    public boolean deleteById(Long mid) {
        TblVehicleMaintenance maintenance = vehicleMaintenanceMapper.selectById(mid);
        if(null != maintenance){
            maintenance.setDeleteTime(LocalDateTime.now());
            maintenance.setDeleteBy( SecurityContextHolder.getUserName());
            QueryWrapper<TblVehicleMaintenance> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().isNull(TblVehicleMaintenance::getDeleteTime).eq(TblVehicleMaintenance::getId,mid);
            return vehicleMaintenanceMapper.update(maintenance,queryWrapper) == 1;
        }
        return false;
    }

    @Override
    public Boolean deleteByVidAndMtime(Long vehicleInfoId, LocalDateTime startTime) {
        LambdaQueryWrapper<TblVehicleMaintenance> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.isNull(TblVehicleMaintenance::getDeleteTime);
        lambdaQueryWrapper.eq(TblVehicleMaintenance::getVehicleInfoId,vehicleInfoId);
        lambdaQueryWrapper.eq(TblVehicleMaintenance::getMaintenanceTime,startTime);
        List<TblVehicleMaintenance> tblVehicleMaintenances = vehicleMaintenanceMapper.selectList(lambdaQueryWrapper);
        tblVehicleMaintenances.forEach(e->{
            e.setDeleteBy(SecurityContextHolder.getUserName());
            e.setDeleteTime(startTime);
            UpdateWrapper<TblVehicleMaintenance> updateQueryWrapper = new UpdateWrapper<>();
            updateQueryWrapper.lambda().eq(TblVehicleMaintenance::getVehicleInfoId,vehicleInfoId);
            updateQueryWrapper.lambda().eq(TblVehicleMaintenance::getMaintenanceTime,startTime);
            vehicleMaintenanceMapper.update(e,updateQueryWrapper);
        });
        return true;
    }

    /**
     * @Author: liwencai
     * @Description: 修改维保信息
     * @Date: 2022/8/2
     * @Param vehicleMaintenanceParamVO:
     * @return: boolean
     */
    @Override
    @Transactional
    public boolean updateSpec(VehicleMaintenanceParamVO vehicleMaintenanceParamVO) {
        TblVehicleMaintenance maintenance = vehicleMaintenanceConverter.toVehicleMaintenance(vehicleMaintenanceParamVO);
        maintenance.setUpdateTime(LocalDateTime.now());
        maintenance.setUpdateBy( SecurityContextHolder.getUserName());
        QueryWrapper<TblVehicleMaintenance> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().isNull(TblVehicleMaintenance::getDeleteTime).eq(TblVehicleMaintenance::getId,vehicleMaintenanceParamVO.getId());
        return vehicleMaintenanceMapper.update(maintenance,queryWrapper) == 1;
    }



    /**
     * @Author: liwencai
     * @Description: 条件查询
     * @Date: 2022/7/28
     * @Param vehicleMaintenanceParamVO:
     * @return: java.util.List<com.thtf.office.entity.TblVehicleMaintenance>
     */
    @Override
    public List<TblVehicleMaintenance> select(VehicleMaintenanceParamVO vehicleMaintenanceParamVO) {
        return vehicleMaintenanceMapper.select(vehicleMaintenanceParamVO);
    }
}
