package com.thtf.office.listener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.thtf.office.common.util.EasyExcelErrorFixUtil;
import com.thtf.office.dto.VehicleInfoExcelErrorImportDTO;
import com.thtf.office.dto.VehicleInfoExcelImportDTO;
import com.thtf.office.dto.converter.VehicleInfoExcelErrorImportConverter;
import com.thtf.office.service.TblVehicleInfoService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * @Author: liwencai
 * @Date: 2022/7/31 19:24
 * @Description: 公车Excel导入监听器 注意：Listener不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
 */
@Slf4j
public class VehicleExcelListener extends AnalysisEventListener<VehicleInfoExcelImportDTO> {

    /**
     * 临时存储解析数据
     */
    private final List<VehicleInfoExcelImportDTO> list = new ArrayList<>();

    /**
     * 每隔5条存储数据库（际使用中可以适当调整），然后清理list，方便内存回收
     */
    private static final int BATCH_COUNT = 50;

    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private final TblVehicleInfoService vehicleInfoService;
    private final HttpServletResponse response;
    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     */
    public VehicleExcelListener(TblVehicleInfoService vehicleInfoService, HttpServletResponse response) {
        this.vehicleInfoService = vehicleInfoService;
        this.response = response;
    }

    // 收集表头和错误数据，利用easyexcel将错误数据返回给用户
    private final List<List<String>> head = new ArrayList<>();
    private final List<List<String>> data = new ArrayList<>();

    public List<List<String>> getHead() {
        return this.head;
    }

    public List<List<String>> getData() {
        return this.data;
    }

    List<VehicleInfoExcelErrorImportDTO> errorList = new ArrayList<>();


    // 如果要收集错误数据，重写表头是必不可少的，因为错误数据中存储的是以列作为键的map集合，并且不会存入为空的数据
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        // 工具类见下
        EasyExcelErrorFixUtil.setExcelErrorHead(headMap, head);
    }

    /**
     * @Author: liwencai
     * @Description: 每一条数据被解析后，都会来调用
     * @Date: 2022/8/1
     * @Param goods:
     * @Param analysisContext:
     * @return: void
     */
    @Override
    public void invoke(VehicleInfoExcelImportDTO dto, AnalysisContext analysisContext) {

        int rowNumber = analysisContext.readRowHolder().getRowIndex();

        StringBuilder stringBuilder = new StringBuilder();
        boolean carNumberMatchesResult = Pattern.matches("^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]$", dto.getCarNumber());
        List<String> errorColumnNames = new ArrayList<>();
        if(! carNumberMatchesResult){
            errorColumnNames.add("carNumber");
            stringBuilder.append("车牌号格式不正确，例：京AA3333;");
            log.info("车牌号格式不正确，例：京AA3333");
        }
        if(StringUtils.isBlank(dto.getVehicleCategoryName())){
            errorColumnNames.add("vehicleCategoryName");
            stringBuilder.append("车辆类别名称不能为空;");
            log.info("车辆类别名称不能为空");
        }
        if(StringUtils.isBlank(dto.getModel())){
            errorColumnNames.add("model");
            stringBuilder.append("车辆厂牌型号不能为空;");
            log.info("车辆厂牌型号不能为空");
        }
        if(StringUtils.isBlank(dto.getEngineNumber())){
            errorColumnNames.add("engineNumber");
            stringBuilder.append("车辆发动机号不能为空;");
            log.info("车辆发动机号不能为空");
        }
        if(StringUtils.isBlank(dto.getFrameNumber())){
            errorColumnNames.add("frameNumber");
            stringBuilder.append("车辆的车架号不能为空");
            log.info("车辆的车架号不能为空");
        }

        if(errorColumnNames.size()>0){
            VehicleInfoExcelErrorImportDTO vehicleInfoExcelErrorImportDTO = Mappers.getMapper(VehicleInfoExcelErrorImportConverter.class).toErrorImport(dto);
            vehicleInfoExcelErrorImportDTO.setRowNumber(String.valueOf(rowNumber));
            vehicleInfoExcelErrorImportDTO.setErrorInfo(stringBuilder.toString());
            errorList.add(vehicleInfoExcelErrorImportDTO);
        }else {
            // 数据存储list中，供批量处理，或后续自己业务逻辑处理。
            list.add(dto);
        }
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if(list.size() >= BATCH_COUNT){
            saveData();
            list.clear();
        }
    }

    /**
     * @Author: liwencai
     * @Description: 所有数据解析完成了，都会调用
     * @Date: 2022/8/1
     * @Param analysisContext:
     * @return: void
     */
    @SneakyThrows
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        response.setCharacterEncoding("utf-8");
        response.setHeader("Pragma", "No-Cache");
        response.setHeader("Cache-Control", "No-Cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("application/vnd.ms-excel; charset=UTF-8");
        ServletOutputStream out = null;

        try {
            response.setHeader("Content-disposition","attachment; filename=" + URLEncoder.encode("公车信息导入模板.xlsx", "UTF-8"));
            out = response.getOutputStream();
            EasyExcel.write(response.getOutputStream(), VehicleInfoExcelErrorImportDTO.class).sheet().doWrite(errorList);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            out.close();
        }

        saveData();
        //向前端写入文件流流

        System.out.println("xxxxxxxxxxxx"+data.toString());
        System.out.println("xxxxxxxxxxxx"+head.toString());
    }

//    // 重写此方法以便于收集错误数据
//    @Override
//    public void onException(Exception exception, AnalysisContext context) {
//        // 调用以下封装的错误数据收集工具
//        EasyExcelErrorFixUtil.setErrorData(exception, context, data, head.size());
//    }

    /**
     * @Author: liwencai
     * @Description: 数据入库操作
     * @Date: 2022/8/8
     * @return: void
     */
    private void saveData() {
        if(list.size() > 0) {
            vehicleInfoService.insertBatch(list);
        }
    }
}
