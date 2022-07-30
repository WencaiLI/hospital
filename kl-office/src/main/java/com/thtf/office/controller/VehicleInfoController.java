package com.thtf.office.controller;

import com.thtf.office.common.response.JsonResult;
import com.thtf.office.common.valid.VehicleParamValid;
import com.thtf.office.dto.VehicleInfoConvert;
import com.thtf.office.vo.VehicleInfoParamVO;
import com.thtf.office.entity.TblVehicleInfo;
import com.thtf.office.service.TblVehicleInfoService;
import com.thtf.office.vo.VehicleSelectByDateResult;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车辆信息表 前端控制器
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
@RestController
@RequestMapping("/vehicle/info")
public class VehicleInfoController {

    @Resource
    TblVehicleInfoService vehicleInfoService;
    @Resource
    VehicleInfoConvert vehicleInfoConvert;

    /**
     * @Author: liwencai
     * @Description: 新增公车信息
     * @Date: 2022/7/26
     * @Param paramVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @PostMapping("/insert")
    public ResponseEntity<JsonResult<Boolean>> insert(@RequestBody @Validated(VehicleParamValid.Insert.class) VehicleInfoParamVO paramVO){
        TblVehicleInfo vehicleInfo = vehicleInfoConvert.toVehicleInfo(paramVO);
        if(vehicleInfoService.insert(vehicleInfo)){
            return ResponseEntity.ok(JsonResult.success(true));
        }else {
            return ResponseEntity.ok(JsonResult.error("新增公车信息失败"));
        }
    }

    /**
     * @Author: liwencai
     * @Description: 删除公车信息
     * @Date: 2022/7/26
     * @Param vid:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @DeleteMapping("/deleteById")
    public ResponseEntity<JsonResult<Boolean>> deleteById(@RequestParam("vid") @NotNull Long vid){
        if(vehicleInfoService.deleteById(vid)){
            return ResponseEntity.ok(JsonResult.success(true));
        }else {
            return ResponseEntity.ok(JsonResult.error("删除公车信息失败"));
        }
    }

    /**
     * @Author: liwencai
     * @Description: 修改公车信息
     * @Date: 2022/7/26
     * @Param paramVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @PutMapping("/update")
    public ResponseEntity<JsonResult<Boolean>> update(@RequestBody @Validated(VehicleParamValid.Update.class) VehicleInfoParamVO paramVO){
        if (vehicleInfoService.updateSpec(paramVO)){
            return ResponseEntity.ok(JsonResult.success(true));
        }else {
            return ResponseEntity.ok(JsonResult.error("修改公车信息失败"));
        }
    }

    /**
     * @Author: liwencai
     * @Description: 查询公车信息
     * @Date: 2022/7/26
     * @Param paramVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.util.List>>
     */
    @PostMapping("/select")
    public ResponseEntity<JsonResult<List<TblVehicleInfo>>> select(@RequestBody VehicleInfoParamVO paramVO){
        return ResponseEntity.ok(JsonResult.success(vehicleInfoService.select(paramVO)));
    }


    /**
     * @Author: liwencai
     * @Description: 公车信息批量导入模板
     * @Date: 2022/7/26
     * @return: void
     */
    @GetMapping("/importTemplateDownload")
    public void importTemplateDownload(){
        // todo 公车模板下载
    }

    /**
     * @Author: liwencai
     * @Description: 公车批量导入
     * @Date: 2022/7/26
     * @return: void
     */
    @GetMapping("/batchImport")
    public void batchImport(){
        // todo 测试批量插入 待完善
        List<TblVehicleInfo> list = new ArrayList<>();
        TblVehicleInfo tblVehicleInfo2 = new TblVehicleInfo();
        tblVehicleInfo2.setId((long) 2222);
        tblVehicleInfo2.setCreateTime(LocalDateTime.now());
        list.add(tblVehicleInfo2);

        for (int i = 0; i < 10; i++) {
            TblVehicleInfo tblVehicleInfo = new TblVehicleInfo();
            tblVehicleInfo.setId((long) i);
            tblVehicleInfo.setCarNumber("京BC0"+i);
            tblVehicleInfo.setCreateTime(LocalDateTime.now());
            list.add(tblVehicleInfo);
        }
        vehicleInfoService.insertBatch(list);
    }

    /**
     * @Author: liwencai
     * @Description: 根据日期和类别查询该类别下汽车的调度排行
     * @Date: 2022/7/26
     * @Param cid:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult>
     */
    @GetMapping("/selectByCidAndMonth")
    public ResponseEntity<JsonResult<List<VehicleSelectByDateResult>>> selectByCidAndMonth(@RequestParam(value = "cid") @NotNull Long cid){
        // todo vehicleInfoService.selectByCidByDate(cid);
        List<VehicleSelectByDateResult> result = vehicleInfoService.selectByCidByDate(cid);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    /**
     * @Author: liwencai
     * @Description: 修改公车状态,此接口前端需定时请求
     * @Date: 2022/7/28
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @GetMapping("/updateInfoStatus")
    public ResponseEntity<JsonResult<Boolean>> updateInfoStatus(){
        if(vehicleInfoService.updateInfoStatus()){
            return ResponseEntity.ok(JsonResult.success(true));
        }else {
            return ResponseEntity.ok(JsonResult.error("更新公车状态失败"));
        }

    }
}
