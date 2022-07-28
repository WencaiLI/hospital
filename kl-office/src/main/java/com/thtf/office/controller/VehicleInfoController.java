package com.thtf.office.controller;

import com.thtf.office.common.dto.adminserver.UserInfo;
import com.thtf.office.common.response.JsonResult;
import com.thtf.office.common.valid.VehicleParamValid;
import com.thtf.office.dto.VehicleInfoConvert;
import com.thtf.office.feign.AdminAPI;
import com.thtf.office.vo.VehicleInfoParamVO;
import com.thtf.office.entity.TblVehicleInfo;
import com.thtf.office.service.TblVehicleInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private AdminAPI adminAPI;

    /**
     * 根据token获取当前用户信息
     * @className userBuildingData
     * @return 当前登录用户信息
     * @Author 邓玉磊
     * @Date 2021/3/16 14:24
     */
    public UserInfo searchUserData(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        UserInfo userInfo = adminAPI.userInfo("de786585-465b-4215-bc74-607e395554ba");
        return userInfo;
    }

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
     * Excel批量导入车辆信息
     *
     * @param type 类型id
     * @param uploadFile 导入文件
     * @return {@link String} 导入情况说明
     * @author guola
     * @date 2022-06-14
     */
    @PostMapping("/itemImport")
    public ResponseEntity<JsonResult<String>> itemImport(HttpServletRequest request, String type, MultipartFile uploadFile) {
        JsonResult result = new JsonResult();
        try{
            UserInfo userDTO = searchUserData(request);
            String originalFilename = uploadFile.getOriginalFilename();
            String string = vehicleInfoService.batchImport(uploadFile, originalFilename, type, userDTO.getRealname());
            result.setData(string);
            result.setStatus("success");
            result.setCode(200);
        } catch (Exception e){
            e.printStackTrace();
            result.setData(e.getClass().getName() + ":" + e.getMessage());
            result.setStatus("error");
            result.setCode(500);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 获取Excel批量导入设备信息进度
     *
     * @author deng
     * @date 2022-06-14
     */
    @GetMapping("/importProgress")
    public ResponseEntity<JsonResult> importProgress() {
        JsonResult result = new JsonResult();
        try{
            BigDecimal map = vehicleInfoService.importProgress();
            result.setData(map);
            result.setCode(200);
            result.setStatus("success");
        } catch (Exception e){
            e.printStackTrace();
            result.setData(e.getClass().getName() + ":" + e.getMessage());
            result.setStatus("error");
            result.setCode(500);
        }
        return ResponseEntity.ok(result);
    }




    /**
     * @Author: liwencai
     * @Description: 公车信息批量导入模板
     * @Date: 2022/7/26
     * @return: void
     */
    @GetMapping("/importTemplateDownload")
    public void importTemplateDownload(HttpServletRequest request, HttpServletResponse response){
        // todo 公车模板下载
        try {
            response.setCharacterEncoding("utf-8");
            response.setHeader("Pragma", "No-Cache");
            response.setHeader("Cache-Control", "No-Cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("application/msexcel; charset=UTF-8");
            response.setHeader("Content-disposition","attachment; filename=" + URLEncoder.encode("设备导入模板.xlsx", "UTF-8"));
            ServletOutputStream out;
            String filePath = this.getClass().getResource("/").getPath().replaceFirst("/", "")
                    + "ExcelTemplate/vehicleTemplate.xlsx";
            FileInputStream in = new FileInputStream(filePath);
            out = response.getOutputStream();
            out.flush();
            int aRead;
            while ((aRead = in.read()) != -1) {
                out.write(aRead);
            }
            out.flush();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * @Description: 查询所有未指派状态的车辆按当月使用次数升序
     * @Date: 2022/7/26
     * @Param cid:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult>
     */
    @GetMapping("/selectByCidAndMonth")
    public ResponseEntity<JsonResult> selectByCidAndMonth(@RequestParam(value = "cid") @NotNull Long cid){
        return ResponseEntity.ok(JsonResult.error("删除公车失败"));
    }
}
