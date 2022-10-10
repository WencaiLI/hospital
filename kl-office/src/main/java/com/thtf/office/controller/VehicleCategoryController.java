package com.thtf.office.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.thtf.common.response.JsonResult;
import com.thtf.office.common.valid.VehicleParamValid;
import com.thtf.office.dto.SelectAllInfoResultDTO;
import com.thtf.office.entity.TblVehicleCategory;
import com.thtf.office.entity.TblVehicleInfo;
import com.thtf.office.service.TblVehicleCategoryService;
import com.thtf.office.service.TblVehicleInfoService;
import com.thtf.office.vo.VehicleCategoryChangeBindVO;
import com.thtf.office.vo.VehicleCategoryParamVO;
import com.thtf.office.vo.VehicleCategoryResultVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

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
     * @return: org.springframework.http.ResponseEntity<com.thtf.common.response.JsonResult<java.lang.Boolean>>
     */
    @PostMapping("/insert")
    public JsonResult<String> insert(@RequestBody @Validated(VehicleParamValid.Insert.class) VehicleCategoryParamVO vehicleCategoryParamVO){
        Map<String, Object> resultMap = vehicleCategoryService.insert(vehicleCategoryParamVO);
        if(resultMap.get("status").equals("error")){
            return JsonResult.error(resultMap.get("errorCause").toString());
        }else {
            return JsonResult.success("新增成功");
        }
    }

    /**
     * @Author: liwencai
     * @Description: 删除公车类别
     * @Date: 2022/7/26
     * @Param cid:
     * @return: org.springframework.http.ResponseEntity<com.thtf.common.response.JsonResult<java.lang.Boolean>>
     */
    @DeleteMapping("/deleteById")
    public JsonResult<Boolean> deleteById(@RequestParam(value = "cid") @NotNull Long cid){
        if(vehicleCategoryService.deleteById(cid)){
            return JsonResult.success(true);
        }else {
            return JsonResult.error("删除公车类别失败");
        }
    }

    /**
     * @Author: liwencai
     * @Description: 更新公车类别
     * @Date: 2022/7/26
     * @Param vehicleCategoryParamVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.common.response.JsonResult<java.lang.Boolean>>
     */
    @PutMapping("/update")
    public JsonResult<Boolean> update(@RequestBody @Validated(VehicleParamValid.Update.class) VehicleCategoryParamVO vehicleCategoryParamVO){
        if(vehicleCategoryService.updateSpec(vehicleCategoryParamVO)){
            return JsonResult.success(true);
        }
        return JsonResult.error("修改公车类别失败");
    }

    /**
     * @Author: liwencai
     * @Description: 查询公车信息
     * @Date: 2022/7/26
     * @Param vehicleCategoryParamVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.common.response.JsonResult<java.awt.List>>
     */
    @PostMapping("/select")
    public JsonResult<List<VehicleCategoryResultVO>> select(@RequestBody VehicleCategoryParamVO vehicleCategoryParamVO){
        return JsonResult.querySuccess(vehicleCategoryService.select(vehicleCategoryParamVO));
    }

    /**
     * @Author: liwencai
     * @Description: 查询公车类别总数
     * @Date: 2022/7/26
     * @return: org.springframework.http.ResponseEntity<com.thtf.common.response.JsonResult<java.lang.Long>>
     */
    @GetMapping("/totalNumber")
    public JsonResult<Integer> totalNumber(){
        QueryWrapper<TblVehicleCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time");
        return JsonResult.querySuccess(vehicleCategoryService.count(queryWrapper));
    }

    /**
     * @Author: liwencai
     * @Description: 查询与公车类别关联的公车数量
     * @Date: 2022/7/26
     * @Param cid:
     * @return: org.springframework.http.ResponseEntity<com.thtf.common.response.JsonResult<java.lang.Integer>>
     */
    @GetMapping("/correlationNumber")
    public JsonResult<Integer> correlationNumber(@RequestParam(value = "cid") @NotNull Long cid){
        QueryWrapper<TblVehicleInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("delete_time").eq("vehicle_category_id",cid);
        return JsonResult.querySuccess(vehicleInfoService.count(queryWrapper));
    }

    /**
     * @Author: liwencai
     * @Description: 移除绑定车辆
     * @Date: 2022/7/26
     * @Param vehicleCategoryChangeBindVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.common.response.JsonResult<java.lang.Boolean>>
     */
    @PostMapping("/changeBind")
    public JsonResult<Boolean> changeBind(@RequestBody VehicleCategoryChangeBindVO vehicleCategoryChangeBindVO){
        if(vehicleCategoryService.changeBind(vehicleCategoryChangeBindVO)){
            return JsonResult.success(true);
        }
        return JsonResult.error("绑定修改失败");
    }

    /**
     * @Author: liwencai 
     * @Description: 查询所有类别对应的各个公车的数量（以公车状态）
     * @Date: 2022/8/2
     * @return: org.springframework.http.ResponseEntity<com.thtf.dto.SelectAllInfoResultDTO>
     */
    @GetMapping("/selectInfoNumberByCategory")
    public JsonResult<List<SelectAllInfoResultDTO>> selectInfoNumberByCategory(){
        return JsonResult.querySuccess(vehicleCategoryService.selectInfoNumberByCategory());
    }

}
