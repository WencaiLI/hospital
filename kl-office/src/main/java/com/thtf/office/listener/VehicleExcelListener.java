package com.thtf.office.listener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.thtf.office.common.util.RegexVerifyUtil;
import com.thtf.office.dto.VehicleInfoExcelErrorImportDTO;
import com.thtf.office.dto.VehicleInfoExcelImportDTO;
import com.thtf.office.dto.converter.VehicleInfoExcelErrorImportConverter;
import com.thtf.office.service.TblVehicleInfoService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.mapstruct.factory.Mappers;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/7/31 19:24
 * @Description: 公车Excel导入监听器 注意：Listener不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
 */
@Slf4j
@SuppressWarnings({"All"})
public class VehicleExcelListener extends AnalysisEventListener<VehicleInfoExcelImportDTO> {

    private final TblVehicleInfoService vehicleInfoService;

    private final HttpServletResponse response;

    public VehicleExcelListener(TblVehicleInfoService vehicleInfoService, HttpServletResponse response) {
        this.vehicleInfoService = vehicleInfoService;
        this.response = response;
    }

    /**
     * 临时存储解析数据
     */
    private final List<VehicleInfoExcelImportDTO> list = new ArrayList<>();

    /**
     * 每隔50条存储数据库（际使用中可以适当调整），然后清理list，方便内存回收
     */
    private static final int BATCH_COUNT = 50;

    /**
     * Excel中数据格式错误数据
     */
    private final List<VehicleInfoExcelErrorImportDTO> errorList = new ArrayList<>();

    /**
     * 已经入库的车牌号
     */
    private final List<String> hadInsertDataCarNumber = new ArrayList<>();

    /**
     * @Author: liwencai
     * @Description: 每一条数据被解析后，都会来调用
     * @Date: 2022/8/1
     * @Param goods:
     * @Param analysisContext:
     * @return: void
     */
    @Override
    @Transactional
    public void invoke(VehicleInfoExcelImportDTO dto, AnalysisContext analysisContext) {


        System.out.println("执行");

        /**
         * 出现错误的列的数量
         */
        int errorColumnNum = 0;
        // 出现错误的行号
        int rowNumber = analysisContext.readRowHolder().getRowIndex();
        // 错误提示信息
        StringBuilder stringBuilder = new StringBuilder();
        // 格式错误数据
        VehicleInfoExcelErrorImportDTO vehicleInfoExcelErrorImportDTO = Mappers.getMapper(VehicleInfoExcelErrorImportConverter.class).toErrorImport(dto);
        /* ************数据验证开始************* */

        /* 车牌小写变大写 */
        if(StringUtils.isNotBlank(dto.getCarNumber())){
            dto.setCarNumber(StringUtils.upperCase(dto.getCarNumber()));
        }

        if(! RegexVerifyUtil.verify(dto.getCarNumber(),RegexVerifyUtil.carNumberRegex)){
            errorColumnNum ++;
            stringBuilder.append("车牌号格式不正确;");
        }else if(hadInsertDataCarNumber.contains(dto.getCarNumber())){
            errorColumnNum ++;
            stringBuilder.append("车牌号已经在Excel中存在");
        } else {
            // 验证车牌号是否在数据库中存在
            if(! vehicleInfoService.verifyCarNumberForInsert(dto.getCarNumber())){
                errorColumnNum ++;
                stringBuilder.append("车牌号已存在;");
            }else{
                hadInsertDataCarNumber.add(dto.getCarNumber());
            }
        }

        if(StringUtils.isBlank(dto.getVehicleCategoryName())){
            errorColumnNum ++;
            stringBuilder.append("车辆类别名称不能为空;");
        }else{
            if(! vehicleInfoService.verifyCategoryForInsert(dto.getVehicleCategoryName())){
                errorColumnNum ++;
                stringBuilder.append("车辆类别名称不存在;");
            }
        }
        if(StringUtils.isBlank(dto.getModel())){
            errorColumnNum ++;
            stringBuilder.append("车辆厂牌型号不能为空;");
        }
        if(StringUtils.isBlank(dto.getEngineNumber())){
            errorColumnNum ++;
            stringBuilder.append("车辆发动机号不能为空;");
        }
        if(StringUtils.isBlank(dto.getFrameNumber())){
            errorColumnNum ++;
            stringBuilder.append("车辆的车架号不能为空");
        }

        if(StringUtils.isNotBlank(dto.getBuyDate())){
            if(RegexVerifyUtil.verify(dto.getBuyDate(),RegexVerifyUtil.number_1)){
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                DateTimeFormatter df_1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                BigDecimal numberValue = new BigDecimal(dto.getBuyDate());
                long second = numberValue.multiply(new BigDecimal("86400")).longValue();
                Instant instant = Instant.ofEpochSecond(second-2209190400L);
                LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                dto.setBuyDate(df.format(localDateTime));
                vehicleInfoExcelErrorImportDTO.setBuyDate(df_1.format(localDateTime));
            }else {
                // 验证日期格式
                if(!RegexVerifyUtil.verify(RegexVerifyUtil.dateRegex_1,dto.getBuyDate())){
                    stringBuilder.append("购买日期格式不正确;");
                }
            }
        }

        if(StringUtils.isNotBlank(dto.getOutDate())){
            if(RegexVerifyUtil.verify(dto.getOutDate(),RegexVerifyUtil.number_1)){
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                DateTimeFormatter df_1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                BigDecimal numberValue = new BigDecimal(dto.getOutDate());
                long second = numberValue.multiply(new BigDecimal("86400")).longValue();
                Instant instant = Instant.ofEpochSecond(second-2209190400L);
                LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                dto.setOutDate(df.format(localDateTime));
                vehicleInfoExcelErrorImportDTO.setOutDate(df_1.format(localDateTime));
            }else {
                // 验证日期格式
                if(!RegexVerifyUtil.verify(RegexVerifyUtil.dateRegex_1,dto.getOutDate())){
                    stringBuilder.append("出厂日期格式不正确，正确示例：2022-01-01;");
                }
            }
        }
        /* ************数据验证结束************* */
        if(errorColumnNum>0){
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
        saveData();
        if(errorList.size()>0){
            responseErrorInfo();
        }
    }

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

    /**
     * @Author: liwencai
     * @Description: 向前端返回错误数据列表
     * @Date: 2022/8/10
     * @return: void
     */

    public void responseErrorInfo(){
//        response.setCharacterEncoding("utf-8");
//        response.setHeader("Pragma", "No-Cache");
//        response.setHeader("Cache-Control", "No-Cache");
//        response.setDateHeader("Expires", 0);
//        response.setContentType("application/vnd.ms-excel");
//        String filename = "excel"+LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+".xls";
//        try {
//            response.setHeader("Content-Disposition","attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        ServletOutputStream out;
        try {
//            //            EasyExcel.write(bos, VehicleInfoExcelErrorImportDTO.class).sheet().doWrite(errorList);
            String fileName = "aaaaaa" + ".xls";
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
//            out = response.getOutputStream();
//            //创建流
////            ByteArrayOutputStream bos=new ByteArrayOutputStream();
//            VehicleInfoExcelErrorImportDTO vehicleInfoExcelErrorImportDTO = new VehicleInfoExcelErrorImportDTO();
//            vehicleInfoExcelErrorImportDTO.setErrorInfo("错误测试");
//            errorList.add(vehicleInfoExcelErrorImportDTO);
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(),VehicleInfoExcelErrorImportDTO.class)
                    .registerWriteHandler(getStyleStrategy())
                    .build();
            WriteSheet writeSheet =  new WriteSheet();
            writeSheet.setSheetName("错误数据");
            excelWriter.write(errorList,writeSheet);
//            WriteSheet writeSheet =new WriteSheet();
//            writeSheet.setHead(Arrays.asList());
//            out.flush();
//            out.close();
            excelWriter.finish();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }


//    public void responseErrorInfo(){
//        response.setCharacterEncoding("utf-8");
////        response.setHeader("Pragma", "No-Cache");
////        response.setHeader("Cache-Control", "No-Cache");
////        response.setDateHeader("Expires", 0);
//        response.setContentType("application/vnd.ms-excel; charset=UTF-8");
////        response.setContentType("application/json; charset=UTF-8");
//        response.setHeader("responseType","blob");
////        response.setContentType("application/octet-stream");
//        response.setHeader("Access-Control-Expose-Headers","Content-Disposition");
////        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        ServletOutputStream out;
//        try {
//            String filename = "excel"+LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+".xls";
//            response.setHeader("Content-Disposition","attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
//            out = response.getOutputStream();
//            //创建流
////            ByteArrayOutputStream bos=new ByteArrayOutputStream();
//            EasyExcel.write(response.getOutputStream(), VehicleInfoExcelErrorImportDTO.class).sheet().doWrite(errorList);
////            EasyExcel.write(bos, VehicleInfoExcelErrorImportDTO.class).sheet().doWrite(errorList);
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error(e.getMessage());
//        }
//    }

    public static HorizontalCellStyleStrategy getStyleStrategy(){
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为灰色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short)12);
        // 字体样式
        headWriteFont.setFontName("宋体");
        headWriteCellStyle.setWriteFont(headWriteFont);
        //自动换行
        headWriteCellStyle.setWrapped(false);
        // 水平对齐方式
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 垂直对齐方式
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
//        contentWriteCellStyle.setFillPatternType(FillPatternType.SQUARES);
        // 背景白色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short)12);
        // 字体样式
        contentWriteFont.setFontName("宋体");
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

}
