package com.thtf.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thtf.office.common.dto.adminserver.UserInfo;
import com.thtf.office.common.response.JsonResult;
import com.thtf.office.common.util.HttpUtil;
import com.thtf.office.common.util.IdGeneratorSnowflake;
import com.thtf.office.dto.converter.VehicleMaintenanceConverter;
import com.thtf.office.feign.AdminAPI;
import com.thtf.office.vo.VehicleMaintenanceParamVO;
import com.thtf.office.entity.TblVehicleMaintenance;
import com.thtf.office.mapper.TblVehicleMaintenanceMapper;
import com.thtf.office.service.TblVehicleMaintenanceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
public class TblVehicleMaintenanceServiceImpl extends ServiceImpl<TblVehicleMaintenanceMapper, TblVehicleMaintenance> implements TblVehicleMaintenanceService {

    @Resource
    TblVehicleMaintenanceMapper vehicleMaintenanceMapper;
    
    @Resource
    VehicleMaintenanceConverter vehicleMaintenanceConverter;

    @Autowired
    private IdGeneratorSnowflake idGeneratorSnowflake;
    
    @Autowired
    AdminAPI adminAPI;

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
        maintenance.setCreateBy(getOperatorName());
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
            maintenance.setDeleteBy(getOperatorName());
            QueryWrapper<TblVehicleMaintenance> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNull("delete_time").eq("id",mid);
            return vehicleMaintenanceMapper.update(maintenance,queryWrapper) == 1;
        }
        return false;
    }

    /**
     * @Author: liwencai
     * @Description: 修改维保信息
     * @Date: 2022/8/2
     * @Param vehicleMaintenanceParamVO:
     * @return: boolean
     */
    @Override
    public boolean updateSpec(VehicleMaintenanceParamVO vehicleMaintenanceParamVO) {
        TblVehicleMaintenance maintenance = vehicleMaintenanceConverter.toVehicleMaintenance(vehicleMaintenanceParamVO);
        maintenance.setUpdateTime(LocalDateTime.now());
        maintenance.setUpdateBy(getOperatorName());
        QueryWrapper<TblVehicleMaintenance> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").eq("id",vehicleMaintenanceParamVO.getId());
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

    /**
     * @Author: liwencai
     * @Description: 获取操作人姓名
     * @Date: 2022/8/2
     * @return: null
     */
    public String getOperatorName(){
        String realName = null;
        UserInfo userInfo = adminAPI.userInfo(HttpUtil.getToken());
        if(null !=  userInfo){
            realName = userInfo.getRealname();
        }
        return realName;
    }
}
