package com.thtf.office.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thtf.common.response.JsonResult;
import com.thtf.office.common.valid.VehicleParamValid;
import com.thtf.office.entity.TblVehicleMaintenance;
import com.thtf.office.service.TblVehicleMaintenanceService;
import com.thtf.office.vo.VehicleMaintenanceParamVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
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

    /**
     * @Author: liwencai
     * @Description: 新增维保信息
     * @Date: 2022/7/26
     * @Param vehicleMaintenanceParamVO:
     * @return: com.thtf.common.response.JsonResult<java.lang.Boolean>
     */
    @PostMapping("/insert")
    public JsonResult<Boolean> insert(@RequestBody @Validated(VehicleParamValid.Insert.class) VehicleMaintenanceParamVO vehicleMaintenanceParamVO){
        if(vehicleMaintenanceService.insert(vehicleMaintenanceParamVO)){
            return JsonResult.success(true);
        }
        return JsonResult.error("新增维保信息失败");
    }

    /**
     * @Author: liwencai
     * @Description: 删除维保信息
     * @Date: 2022/7/26
     * @Param mid:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.lang.Boolean>
     */
    @DeleteMapping("/deleteById")
    public JsonResult<Boolean> deleteById(@RequestParam("mid") @NotNull Long mid){
        if (vehicleMaintenanceService.deleteById(mid)){
            return JsonResult.success(true);
        }
        return JsonResult.error("删除维保信息失败");
    }

    /**
     * @Author: liwencai
     * @Description: 修改维保信息
     * @Date: 2022/7/26
     * @Param vehicleMaintenanceParamVO:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.lang.Boolean>>
     */
    @PutMapping("/update")
    public JsonResult<Boolean> update(@RequestBody @Validated(VehicleParamValid.Update.class) VehicleMaintenanceParamVO vehicleMaintenanceParamVO){
        if (vehicleMaintenanceService.updateSpec(vehicleMaintenanceParamVO)){
            return JsonResult.success(true);
        }
        return JsonResult.error("修改维保消息失败");
    }

    /**
     * @Author: liwencai
     * @Description: 查询维保信息
     * @Date: 2022/7/26
     * @Param vehicleMaintenanceParamVO:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.util.List>>
     */
    @PostMapping("/select")
    public JsonResult<PageInfo<TblVehicleMaintenance>> select(@RequestBody VehicleMaintenanceParamVO vehicleMaintenanceParamVO){
        if(null != vehicleMaintenanceParamVO.getPageNumber() && null != vehicleMaintenanceParamVO.getPageSize()){
            PageHelper.startPage(vehicleMaintenanceParamVO.getPageNumber(),vehicleMaintenanceParamVO.getPageSize());
        }
        return JsonResult.querySuccess(PageInfo.of(vehicleMaintenanceService.select(vehicleMaintenanceParamVO)));
    }
}
