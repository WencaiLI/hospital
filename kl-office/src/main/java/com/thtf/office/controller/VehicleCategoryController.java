package com.thtf.office.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thtf.office.common.response.JsonResult;
import com.thtf.office.common.valid.VehicleParamValid;
import com.thtf.office.entity.TblVehicleInfo;
import com.thtf.office.service.TblVehicleInfoService;
import com.thtf.office.vo.VehicleCategoryChangeBindVO;
import com.thtf.office.vo.VehicleCategoryParamVO;
import com.thtf.office.entity.TblVehicleCategory;
import com.thtf.office.service.TblVehicleCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 车辆类别表 前端控制器
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
@RestController
@RequestMapping("/vehicle/category")
public class VehicleCategoryController {

    @Resource
    TblVehicleCategoryService vehicleCategoryService;
    @Resource
    TblVehicleInfoService vehicleInfoService;

    /**
     * @Author: liwencai
     * @Description: 新增公车类别
     * @Date: 2022/7/26
     * @Param vehicleCategoryParamVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @PostMapping("/insert")
    public ResponseEntity<JsonResult<Boolean>> insert(@RequestBody @Validated(VehicleParamValid.Insert.class) VehicleCategoryParamVO vehicleCategoryParamVO){
        if(vehicleCategoryService.insert(vehicleCategoryParamVO)){
            return ResponseEntity.ok(JsonResult.success(true));
        }else {
            return ResponseEntity.ok(JsonResult.error("添加公车类别失败"));
        }
    }

    /**
     * @Author: liwencai
     * @Description: 删除公车类别
     * @Date: 2022/7/26
     * @Param cid:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @DeleteMapping("/deleteById")
    public ResponseEntity<JsonResult<Boolean>> deleteById(@RequestParam(value = "cid") @NotNull Long cid){
        if(vehicleCategoryService.deleteById(cid)){
            return ResponseEntity.ok(JsonResult.success(true));
        }else {
            return ResponseEntity.ok(JsonResult.error("删除公车类别失败"));
        }
    }

    /**
     * @Author: liwencai
     * @Description: 更新公车类别
     * @Date: 2022/7/26
     * @Param vehicleCategoryParamVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @PutMapping("/update")
    public ResponseEntity<JsonResult<Boolean>> update(@RequestBody @Validated(VehicleParamValid.Update.class) VehicleCategoryParamVO vehicleCategoryParamVO){
        if(vehicleCategoryService.updateSpec(vehicleCategoryParamVO)){
            return ResponseEntity.ok(JsonResult.success(true));
        }
        return ResponseEntity.ok(JsonResult.error("修改公车类别失败"));
    }

    /**
     * @Author: liwencai
     * @Description: 查询公车信息
     * @Date: 2022/7/26
     * @Param vehicleCategoryParamVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.awt.List>>
     */
    @PostMapping("/select")
    public ResponseEntity<JsonResult<List>> select(@RequestBody VehicleCategoryParamVO vehicleCategoryParamVO){
        List vehicleCategoryList =vehicleCategoryService.select(vehicleCategoryParamVO);
        return ResponseEntity.ok(JsonResult.success(vehicleCategoryList));
    }

    /**
     * @Author: liwencai
     * @Description: 查询公车类别总数
     * @Date: 2022/7/26
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Long>>
     */
    @GetMapping("/totalNumber")
    public ResponseEntity<JsonResult<Integer>> totalNumber(){
        QueryWrapper<TblVehicleCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time");
        vehicleCategoryService.count(queryWrapper);
        return ResponseEntity.ok(JsonResult.success(vehicleCategoryService.count(queryWrapper)));
    }

    /**
     * @Author: liwencai
     * @Description: 查询与公车类别关联的公车数量
     * @Date: 2022/7/26
     * @Param cid:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Integer>>
     */
    @GetMapping("/correlationNumber")
    public ResponseEntity<JsonResult<Integer>> correlationNumber(@RequestParam(value = "cid") @NotNull Long cid){
        QueryWrapper<TblVehicleInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").eq("vehicle_category_id",cid);
        return ResponseEntity.ok(JsonResult.success(vehicleInfoService.count(queryWrapper)));
    }

    /**
     * @Author: liwencai
     * @Description: 移除绑定车辆
     * @Date: 2022/7/26
     * @Param vehicleCategoryChangeBindVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @PostMapping("/changeBind")
    public ResponseEntity<JsonResult<Boolean>> changeBind(@RequestBody VehicleCategoryChangeBindVO vehicleCategoryChangeBindVO){
        // todo 移除绑定功能待做
        if(vehicleCategoryService.changeBind(vehicleCategoryChangeBindVO)){
            return ResponseEntity.ok(JsonResult.success(true));
        }
        return ResponseEntity.ok(JsonResult.error("绑定修改失败"));
    }
}
