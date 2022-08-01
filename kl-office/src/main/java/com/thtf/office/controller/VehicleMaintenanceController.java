package com.thtf.office.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thtf.office.common.response.JsonResult;
import com.thtf.office.common.util.IdGeneratorSnowflake;
import com.thtf.office.common.valid.VehicleParamValid;
import com.thtf.office.dto.converter.VehicleMaintenanceConverter;
import com.thtf.office.vo.VehicleMaintenanceParamVO;
import com.thtf.office.entity.TblVehicleMaintenance;
import com.thtf.office.service.TblVehicleMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 车辆维保表 前端控制器
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
@RestController
@RequestMapping("/vehicle/maintenance")
public class VehicleMaintenanceController {

    @Resource
    TblVehicleMaintenanceService vehicleMaintenanceService;

    @Resource
    VehicleMaintenanceConverter vehicleMaintenanceConverter;

    @Autowired
    private IdGeneratorSnowflake idGeneratorSnowflake;

    /**
     * @Author: liwencai
     * @Description: 新增维保信息
     * @Date: 2022/7/26
     * @Param vehicleMaintenanceParamVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @PostMapping("/insert")
    public ResponseEntity<JsonResult<Boolean>> insert(@RequestBody @Validated(VehicleParamValid.Insert.class) VehicleMaintenanceParamVO vehicleMaintenanceParamVO){
        TblVehicleMaintenance maintenance = vehicleMaintenanceConverter.toVehicleMaintenance(vehicleMaintenanceParamVO);
        maintenance.setCreateTime(LocalDateTime.now());
        // todo maintenance.setCreateBy();
        if(vehicleMaintenanceService.save(maintenance)){
            return ResponseEntity.ok(JsonResult.success(true));
        }
        return ResponseEntity.ok(JsonResult.error("新增维保信息失败"));
    }

    /**
     * @Author: liwencai
     * @Description: 删除维保信息
     * @Date: 2022/7/26
     * @Param mid:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @DeleteMapping("/deleteById")
    public ResponseEntity<JsonResult<Boolean>> deleteById(@RequestParam("mid") @NotNull Long mid){
        if (vehicleMaintenanceService.deleteById(mid)){
            return ResponseEntity.ok(JsonResult.success(true));
        }
        return ResponseEntity.ok(JsonResult.error("删除维保信息失败"));
    }

    /**
     * @Author: liwencai
     * @Description: 修改维保信息
     * @Date: 2022/7/26
     * @Param vehicleMaintenanceParamVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @PutMapping("/update")
    public ResponseEntity<JsonResult<Boolean>> update(@RequestBody @Validated(VehicleParamValid.Update.class) VehicleMaintenanceParamVO vehicleMaintenanceParamVO){
        TblVehicleMaintenance maintenance = vehicleMaintenanceConverter.toVehicleMaintenance(vehicleMaintenanceParamVO);
        maintenance.setUpdateTime(LocalDateTime.now());
        // todo maintenance.setUpdateBy();
        QueryWrapper<TblVehicleMaintenance> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").eq("id",vehicleMaintenanceParamVO.getId());
        boolean x = vehicleMaintenanceService.update(maintenance,queryWrapper);
        if (x){
            return ResponseEntity.ok(JsonResult.success(true));
        }
        return ResponseEntity.ok(JsonResult.error("修改维保消息失败"));
    }

    /**
     * @Author: liwencai
     * @Description: 查询维保信息
     * @Date: 2022/7/26
     * @Param vehicleMaintenanceParamVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.util.List>>
     */
    @PostMapping("/select")
    public ResponseEntity<JsonResult<List<TblVehicleMaintenance>>> select(@RequestBody VehicleMaintenanceParamVO vehicleMaintenanceParamVO){
        List<TblVehicleMaintenance> result = vehicleMaintenanceService.select(vehicleMaintenanceParamVO);
        return ResponseEntity.ok(JsonResult.success(result));
    }
}
