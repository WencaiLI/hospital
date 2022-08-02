package com.thtf.office.controller;

import com.thtf.office.common.response.JsonResult;
import com.thtf.office.common.valid.VehicleParamValid;
import com.thtf.office.dto.converter.VehicleSchedulingConverter;
import com.thtf.office.vo.VehicleSchedulingParamVO;
import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.service.TblVehicleSchedulingService;
import com.thtf.office.vo.VehicleSelectByDateResult;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
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

    /**
     * @Author: liwencai
     * @Description: 新增调度记录
     * @Date: 2022/7/26
     * @Param paramVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @PostMapping("/insert")
    public ResponseEntity<JsonResult<Boolean>> insert(@RequestBody @Validated(VehicleParamValid.Insert.class) VehicleSchedulingParamVO paramVO){
        if(vehicleSchedulingService.insert(paramVO)){
            return ResponseEntity.ok(JsonResult.success(true));
        }
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
        if(vehicleSchedulingService.updateSpec(paramVO)){
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

    /**
     * @Author: liwencai
     * @Description: 查询待命状态的司机的日、月出车情况
     * @Date: 2022/7/29
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.util.List<com.thtf.office.vo.VehicleSelectByDateResult>>>
     */
    @GetMapping("/selectInfoAboutDri")
    public ResponseEntity<JsonResult<List<VehicleSelectByDateResult>>> selectInfoAboutDri() {
        List<VehicleSelectByDateResult> results = vehicleSchedulingService.selectInfoAboutDri();
        return ResponseEntity.ok(JsonResult.success(results));
    }

    /**
     * @Description 生成最新的调度流水号
     * @param
     * @return  调度流水号字符串
     * @author guola
     * @date 2022-07-28
     */
    @GetMapping("/createSerialNumber")
    public ResponseEntity<JsonResult<String>> createSerialNumber() {
        JsonResult<String> result = new JsonResult<>();
        try {
            String num = vehicleSchedulingService.createSerialNumber();
            result.setCode(200);
            result.setData(num);
            result.setStatus("success");
        } catch (Exception e) {
            e.printStackTrace();
            result.setData(e.getClass().getName() + ":" + e.getMessage());
            result.setStatus("error");
            result.setCode(500);
        }
        return ResponseEntity.ok(result);
    }
}
