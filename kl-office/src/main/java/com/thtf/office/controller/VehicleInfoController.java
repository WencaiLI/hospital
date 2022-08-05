package com.thtf.office.controller;

import com.alibaba.excel.EasyExcel;
import com.thtf.office.common.response.JsonResult;
import com.thtf.office.common.util.FileUtil;
import com.thtf.common.dto.adminserver.UserInfo;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.response.JsonResult;
import com.thtf.common.util.FileUtil;
import com.thtf.office.common.valid.VehicleParamValid;
import com.thtf.office.dto.VehicleInfoExcelImportDTO;
import com.thtf.office.dto.converter.VehicleInfoConverter;
import com.thtf.office.listener.VehicleExcelListener;
import com.thtf.office.vo.VehicleInfoParamVO;
import com.thtf.office.entity.TblVehicleInfo;
import com.thtf.office.listener.VehicleExcelListener;
import com.thtf.office.service.TblVehicleInfoService;
import com.thtf.office.vo.VehicleInfoParamVO;
import com.thtf.office.vo.VehicleSelectByDateResult;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
@RequestMapping("/vehicle/info")
public class VehicleInfoController {

    @Resource
    TblVehicleInfoService vehicleInfoService;

    @Resource
    VehicleInfoConverter vehicleInfoConverter;

    @Autowired
    private FileUtil fileUtil;

    /**
     * @Author: liwencai
     * @Description: 新增公车信息
     * @Date: 2022/7/26
     * @Param paramVO:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.lang.Boolean>>
     */
    @PostMapping("/insert")
    public ResponseEntity<JsonResult<Boolean>> insert(@Validated(VehicleParamValid.Insert.class) VehicleInfoParamVO paramVO,
                                                      @ModelAttribute List<MultipartFile> carImageFile,@ModelAttribute List<MultipartFile> drivingBookImageFile) throws Exception {

        //上传文件后获取文件名字符串和url字符串
        String[] carImageFileNameAndUrl = fileUtil.uploadMultiFile(carImageFile);
        paramVO.setCarImage(carImageFileNameAndUrl[0]);
        paramVO.setCarImageUrl(carImageFileNameAndUrl[1]);
        String[] bookImageFileNameAndUrl = fileUtil.uploadMultiFile(drivingBookImageFile);
        paramVO.setDrivingBookImage(bookImageFileNameAndUrl[0]);
        paramVO.setDrivingBookImageUrl(bookImageFileNameAndUrl[1]);
        TblVehicleInfo vehicleInfo = vehicleInfoConverter.toVehicleInfo(paramVO);

        Map<String, Object> result = vehicleInfoService.insert(vehicleInfo);
        if(result.get("status").equals("success")){
            return ResponseEntity.ok(JsonResult.success(true));
        }else {
            return ResponseEntity.ok(JsonResult.error(result.get("errorCause").toString()));
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
    public ResponseEntity<JsonResult<Boolean>> update(@Validated(VehicleParamValid.Update.class) VehicleInfoParamVO paramVO,
                                                      @ModelAttribute List<MultipartFile> carImageFile,@ModelAttribute List<MultipartFile> drivingBookImageFile) throws Exception {


        //上传文件后获取文件名字符串和url字符串
        if(carImageFile.size() > 0){
            String[] carImageFileNameAndUrl = fileUtil.uploadMultiFile(carImageFile);
            paramVO.setCarImage(carImageFileNameAndUrl[0]);
            paramVO.setCarImageUrl(carImageFileNameAndUrl[1]);
        }
        if(drivingBookImageFile.size() > 0){
            String[] bookImageFileNameAndUrl = fileUtil.uploadMultiFile(drivingBookImageFile);
            paramVO.setDrivingBookImage(bookImageFileNameAndUrl[0]);
            paramVO.setDrivingBookImageUrl(bookImageFileNameAndUrl[1]);
        }
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
     * @Description: 关键词模糊查询
     * @Date: 2022/8/4
     * @Param keywords:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.util.List<com.thtf.office.entity.TblVehicleInfo>>>
     */
    @GetMapping("/selectByKey")
    public ResponseEntity<JsonResult<List<TblVehicleInfo>>> selectByKey(@NotNull @RequestParam(value="key") String keywords){
        return ResponseEntity.ok(JsonResult.success(vehicleInfoService.selectByKey(keywords)));
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
    public JsonResult<String> itemImport(HttpServletRequest request, String type, MultipartFile uploadFile) {
        try{
//            UserInfo userDTO = HttpUtil.getUserInfo();
            String originalFilename = uploadFile.getOriginalFilename();
            String string = vehicleInfoService.batchImport(uploadFile, originalFilename, type, userDTO.getRealname());
            return JsonResult.success(string);
//            result.setData(string);
//            result.setStatus("success");
//            result.setCode(200);
            String string = vehicleInfoService.batchImport(uploadFile, originalFilename, type, null);
            result.setData(string);
            result.setStatus("success");
            result.setCode(200);
        } catch (Exception e){
            log.error(e.getClass().getName() + ":" + e.getMessage());
            return JsonResult.error(e.getClass().getName() + ":" + e.getMessage());
//            e.printStackTrace();
//            result.setData(e.getClass().getName() + ":" + e.getMessage());
//            result.setStatus("error");
//            result.setCode(500);
        }
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
     * @Description: 公车信息批量导入模板下载
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
            System.out.println(filePath);
            String path = this.getClass().getClassLoader().getResource("").getPath();//注意getResource("")里面是空字符串
            System.out.println(path);
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
    public void batchImport(@ModelAttribute MultipartFile uploadFile) throws IOException {
        EasyExcel.read(uploadFile.getInputStream(), VehicleInfoExcelImportDTO.class, new VehicleExcelListener(vehicleInfoService)).headRowNumber(3).sheet().doRead();
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
