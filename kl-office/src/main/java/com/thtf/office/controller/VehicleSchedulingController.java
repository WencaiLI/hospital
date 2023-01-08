package com.thtf.office.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.adminserver.TblOrganizationDTO;
import com.thtf.common.entity.adminserver.TblUser;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.log.OperateLog;
import com.thtf.common.log.OperateType;
import com.thtf.common.response.JsonResult;
import com.thtf.office.common.valid.VehicleParamValid;
import com.thtf.office.entity.TblVehicleScheduling;
import com.thtf.office.service.TblVehicleSchedulingService;
import com.thtf.office.vo.VehicleSchedulingParamVO;
import com.thtf.office.vo.VehicleSelectByDateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车辆调度表 前端控制器
 * </p>
 *
 * @author guola
 * @since 2022-07-26
 */
@Slf4j
@RestController
@RequestMapping("/vehicle/scheduling")
public class VehicleSchedulingController {

    @Resource
    private TblVehicleSchedulingService vehicleSchedulingService;

    @Autowired
    private AdminAPI adminAPI;

    /**
     * @Author: liwencai
     * @Description: 新增调度记录
     * @Date: 2022/7/26
     * @Param paramVO:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.lang.Boolean>
     */
    @PostMapping("/insert")
    @OperateLog(content = "新增调度记录",operateType = OperateType.INSERT,systemCode = "servers.office-server.code",systemName = "servers.office-server.name")
    public JsonResult<Boolean> insert(@RequestBody @Validated(VehicleParamValid.Insert.class) VehicleSchedulingParamVO paramVO){
        Map<String, Object> resultMap = vehicleSchedulingService.insert(paramVO);
        if(resultMap.get("status").equals("error")){
            return JsonResult.error(resultMap.get("errorCause").toString());
        }else {
            return JsonResult.success(true);
        }
    }

    /**
     * @Author: liwencai
     * @Description: 删除调度记录
     * @Date: 2022/7/26
     * @Param sid:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.lang.Boolean>
     */
    @DeleteMapping("/deleteById")
    @OperateLog(content = "删除调度记录",operateType = OperateType.DELETE,systemCode = "servers.office-server.code",systemName = "servers.office-server.name")
    public JsonResult<Boolean> deleteById(@RequestParam("sid") Long sid) {
        if(vehicleSchedulingService.deleteById(sid)){
            return JsonResult.success(true);
        }
        return JsonResult.error("删除调度记录失败");
    }

    /**
     * @Author: liwencai
     * @Description: 修改调度信息
     * @Date: 2022/7/26
     * @Param paramVO:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.lang.Boolean>
     */
    @PutMapping("/update")
    @OperateLog(content = "修改调度信息",operateType = OperateType.UPDATE,systemCode = "servers.office-server.code",systemName = "servers.office-server.name")
    public JsonResult<Boolean> update(@RequestBody @Validated(VehicleParamValid.Update.class) VehicleSchedulingParamVO paramVO){
        Map<String, Object> resultMap = vehicleSchedulingService.updateSpec(paramVO);
        if(resultMap.get("status").equals("error")){
            return JsonResult.error(resultMap.get("errorCause").toString());
        }else {
            return JsonResult.success(true);
        }
    }

    /**
     * @Author: liwencai
     * @Description: 查询调度记录
     * @Date: 2022/7/26
     * @Param paramVO:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.util.List<com.thtf.entity.TblVehicleScheduling>>
     */
    @PostMapping("/select")
    public JsonResult<PageInfo<TblVehicleScheduling>> select(@RequestBody VehicleSchedulingParamVO paramVO){
        if(null != paramVO.getPageNumber() && null != paramVO.getPageSize()){
            PageHelper.startPage(paramVO.getPageNumber(),paramVO.getPageSize());
        }
        return JsonResult.querySuccess(PageInfo.of(vehicleSchedulingService.select(paramVO)));
    }

    /**
     * @Author: liwencai
     * @Description: 查询待命状态的司机的日、月出车情况
     * @Date: 2022/7/29
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.util.List<com.thtf.vo.VehicleSelectByDateResult>>
     */
    @GetMapping("/selectInfoAboutDri")
    public JsonResult<List<VehicleSelectByDateResult>> selectInfoAboutDri() {
        return JsonResult.querySuccess(vehicleSchedulingService.selectInfoAboutDri());
    }

    /**
     * @Author: liwencai
     * @Description: 生成最新的调度流水号
     * @Date: 2022-07-28
     * @Return: com.thtf.common.response.JsonResult<java.lang.String>
     */
    @GetMapping("/createSerialNumber")
    public JsonResult<String> createSerialNumber() {
        try {
            return JsonResult.querySuccess(vehicleSchedulingService.createSerialNumber());
        } catch (Exception e) {
            return JsonResult.error("流水号生成失败！");
        }
    }

    /**
     * @Author: liwencai
     * @Description: 查询所有部门信息
     * @Date: 2022/8/3
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.util.List<com.thtf.common.dto.adminserver.TblOrganizationDTO>>
     */
    @GetMapping("/findOrganizationTree")
    JsonResult<List<TblOrganizationDTO>> findOrganizationTree(){
        try {
            return JsonResult.querySuccess(adminAPI.findOrganizationTree().getData());
        }catch (Exception e) {
            return JsonResult.error("查询失败");
        }
    }

    /**
     * @Author: liwencai
     * @Description: 通过组织编码查询用户信息
     * @Date: 2022/8/3
     * @Param organizationCode: 组织编码
     * @return: org.springframework.http.ResponseEntity<com.thtf.common.response.JsonResult<java.util.List<com.thtf.common.entity.adminserver.TblUser>>>
     */
    @GetMapping("/searchUserByOrganization")
    ResponseEntity<JsonResult<List<TblUser>>> searchUserByOrganization(@RequestParam(value = "organizationCode") String organizationCode){
        try {
            return adminAPI.searchUserByOrganization(organizationCode);
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.ok(JsonResult.error("查询失败"));
        }
    }
}
