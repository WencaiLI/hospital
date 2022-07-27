package com.thtf.office.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thtf.office.common.response.JsonResult;
import com.thtf.office.common.util.IdGeneratorSnowflake;
import com.thtf.office.common.valid.VehicleParamValid;
import com.thtf.office.dto.VehicleSchedulingConvert;
import com.thtf.office.vo.VehicleSchedulingParamVO;
import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.service.TblVehicleSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 车辆调度表 前端控制器
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
@RestController
@RequestMapping("/vehicle/scheduling")
public class VehicleSchedulingController {

    @Resource
    TblVehicleSchedulingService vehicleSchedulingService;

    @Resource
    VehicleSchedulingConvert vehicleSchedulingConvert;

    @Autowired
    private IdGeneratorSnowflake idGeneratorSnowflake;

    /**
     * @Author: liwencai
     * @Description: 新增调度记录
     * @Date: 2022/7/26
     * @Param paramVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @PostMapping("/insert")
    public ResponseEntity<JsonResult<Boolean>> insert(@RequestBody @Validated(VehicleParamValid.Insert.class) VehicleSchedulingParamVO paramVO){
        TblVehicleScheduling scheduling = vehicleSchedulingConvert.toVehicleScheduling(paramVO);
        scheduling.setCreateTime(LocalDateTime.now());
        // todo scheduling.setCreateBy();
        if(vehicleSchedulingService.save(scheduling)){
            return ResponseEntity.ok(JsonResult.success(true));
        }
        // todo 修改公车的状态
        return ResponseEntity.ok(JsonResult.error("新增调度记录失败"));
    }

    /**
     * @Author: liwencai
     * @Description: 删除调度记录
     * @Date: 2022/7/26
     * @Param sid:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @DeleteMapping("/deleteById")
    public ResponseEntity<JsonResult<Boolean>> deleteById(@RequestParam("sid") Long sid) {
        if(vehicleSchedulingService.deleteById(sid)){
            return ResponseEntity.ok(JsonResult.success(true));
        }
        return ResponseEntity.ok(JsonResult.error("删除调度记录失败"));
    }

    /**
     * @Author: liwencai
     * @Description: 修改调度信息
     * @Date: 2022/7/26
     * @Param paramVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @PutMapping("/update")
    public ResponseEntity<JsonResult<Boolean>> update(@RequestBody @Validated(VehicleParamValid.Update.class) VehicleSchedulingParamVO paramVO){
        TblVehicleScheduling scheduling = vehicleSchedulingConvert.toVehicleScheduling(paramVO);
        scheduling.setUpdateTime(LocalDateTime.now());
        // todo tblVehicleScheduling.setUpdateBy
        QueryWrapper<TblVehicleScheduling> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").eq("id",paramVO.getId());
        if(vehicleSchedulingService.update(scheduling,queryWrapper)){
            return ResponseEntity.ok(JsonResult.success(true));
        }
        return ResponseEntity.ok(JsonResult.error("新增调度记录失败"));
    }

    /**
     * @Author: liwencai
     * @Description: 查询调度记录
     * @Date: 2022/7/26
     * @Param paramVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.util.List<com.thtf.office.entity.TblVehicleScheduling>>>
     */
    @PostMapping("/select")
    public ResponseEntity<JsonResult<List<TblVehicleScheduling>>> select(@RequestBody VehicleSchedulingParamVO paramVO){
        List<TblVehicleScheduling> result = vehicleSchedulingService.select(paramVO);
        return ResponseEntity.ok(JsonResult.success(result));
    }
}
