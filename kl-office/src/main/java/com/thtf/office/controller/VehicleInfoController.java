package com.thtf.office.controller;

import cn.hutool.db.Page;
import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thtf.common.response.JsonResult;
import com.thtf.common.util.FileUtil;
import com.thtf.office.common.valid.VehicleParamValid;
import com.thtf.office.dto.VehicleInfoExcelImportDTO;
import com.thtf.office.dto.converter.VehicleInfoConverter;
import com.thtf.office.listener.VehicleExcelListener;
import com.thtf.office.vo.VehicleInfoParamVO;
import com.thtf.office.entity.TblVehicleInfo;
import com.thtf.office.service.TblVehicleInfoService;
import com.thtf.office.vo.VehicleSelectByDateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.lang.Boolean>>
     */
    @PostMapping("/insert")
    public JsonResult<Boolean> insert(@Validated(VehicleParamValid.Insert.class) VehicleInfoParamVO paramVO,
                                      @ModelAttribute List<MultipartFile> carImageFile,
                                      @ModelAttribute List<MultipartFile> drivingBookImageFile) throws Exception {

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
            return JsonResult.success(true);
        }else {
            return JsonResult.error(result.get("errorCause").toString());
        }
    }

    /**
     * @Author: liwencai
     * @Description: 删除公车信息
     * @Date: 2022/7/26
     * @Param vid: 公车id
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.lang.Boolean>>
     */
    @DeleteMapping("/deleteById")
    public JsonResult<Boolean> deleteById(@RequestParam("vid") @NotNull Long vid){
        if(vehicleInfoService.deleteById(vid)){
            return JsonResult.success(true);
        }else {
            return JsonResult.error("删除公车信息失败");
        }
    }

    /**
     * @Author: liwencai
     * @Description: 修改公车信息
     * @Date: 2022/7/26
     * @Param paramVO:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.lang.Boolean>>
     */
    @PutMapping("/update")
    public JsonResult<Boolean> update(@Validated(VehicleParamValid.Update.class) VehicleInfoParamVO paramVO,
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
            return JsonResult.success(true);
        }else {
            return JsonResult.error("修改公车信息失败");
        }
    }

    /**
     * @Author: liwencai
     * @Description: 查询公车信息
     * @Date: 2022/7/26
     * @Param paramVO:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.util.List>>
     */
    @PostMapping("/select")
    public JsonResult<PageInfo<TblVehicleInfo>> select(@RequestBody VehicleInfoParamVO paramVO){
        if(null != paramVO.getPageNumber()  && null != paramVO.getPageSize()){
            PageHelper.startPage(paramVO.getPageNumber(),paramVO.getPageSize());
        }
        return JsonResult.querySuccess(PageInfo.of(vehicleInfoService.select(paramVO)));
    }

    /**
     * @Author: liwencai
     * @Description: 关键词模糊查询(车牌号)
     * @Date: 2022/8/4
     * @Param keywords:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.util.List<com.thtf.entity.TblVehicleInfo>>>
     */
    @GetMapping("/selectByKey")
    public JsonResult<List<TblVehicleInfo>> selectByKey(@NotNull @RequestParam(value="key") String keywords){
        return JsonResult.querySuccess(vehicleInfoService.selectByKey(keywords));
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
        return JsonResult.success(vehicleInfoService.batchImport(uploadFile, uploadFile.getOriginalFilename(), type, null));
    }

    /**
     * 获取Excel批量导入设备信息进度
     *
     * @author deng
     * @date 2022-06-14
     */
    @GetMapping("/importProgress")
    public JsonResult<BigDecimal> importProgress() {
        try{
            return JsonResult.querySuccess(vehicleInfoService.importProgress());
        } catch (Exception e){
            e.printStackTrace();
            return JsonResult.error(e.getClass().getName() + ":" + e.getMessage());
        }
    }


    /**
     * @Author: liwencai
     * @Description: 公车信息批量导入模板下载
     * @Date: 2022/7/26
     * @return: void
     */
    @GetMapping("/importTemplateDownload")
    public void importTemplateDownload(HttpServletRequest request, HttpServletResponse response){

        response.setCharacterEncoding("utf-8");
        response.setHeader("Pragma", "No-Cache");
        response.setHeader("Cache-Control", "No-Cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("application/msexcel; charset=UTF-8");
        try {
            response.setHeader("Content-disposition","attachment; filename=" + URLEncoder.encode("公车信息导入模板.xlsx", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//            String filePath = this.getClass().getResource("/").getPath().replaceFirst("/", "")
//                    + "ExcelTemplate/vehicleTemplate.xlsx";
//            String path = this.getClass().getClassLoader().getResource("").getPath();//注意getResource("")里面是空字符串
//            FileInputStream in = new FileInputStream(filePath);
//            out = response.getOutputStream();
//            out.flush();
//            int aRead;
//            while ((aRead = in.read()) != -1) {
//                out.write(aRead);
//            }
//            out.flush();
//            in.close();
//            out.close();

        InputStream inputStream ;
        ServletOutputStream out ;
        try {
            out = response.getOutputStream();
            inputStream = this.getClass().getClassLoader().getResourceAsStream("ExcelTemplate/vehicleTemplate.xlsx");
            int aRead;
            while ((aRead = inputStream.read()) != -1) {
                out.write(aRead);
            }
            out.close();
            inputStream.close();
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
    @PostMapping("/batchImport")
    public void batchImport(@ModelAttribute MultipartFile uploadFile,HttpServletResponse response) throws IOException {
        try {
            EasyExcel.read(uploadFile.getInputStream(), VehicleInfoExcelImportDTO.class, new VehicleExcelListener(vehicleInfoService,response)).headRowNumber(3).sheet().doRead();
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    /**
     * @Author: liwencai
     * @Description: 根据日期和类别查询该类别下汽车的调度排行
     * @Date: 2022/7/26
     * @Param cid:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult>
     */
    @GetMapping("/selectByCidAndMonth")
    public JsonResult<List<VehicleSelectByDateResult>> selectByCidAndMonth(@RequestParam(value = "cid") @NotNull Long cid){
        List<VehicleSelectByDateResult> result = vehicleInfoService.selectByCidByDate(cid);
        return JsonResult.querySuccess(result);
    }

    /**
     * @Author: liwencai
     * @Description: 修改公车状态,此接口前端需定时请求
     * @Date: 2022/7/28
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.lang.Boolean>>
     */
    @GetMapping("/updateInfoStatus")
    public JsonResult<Boolean> updateInfoStatus(){
        return JsonResult.querySuccess(true);
    }
}
