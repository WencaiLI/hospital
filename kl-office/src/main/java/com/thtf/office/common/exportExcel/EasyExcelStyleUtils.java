package com.thtf.office.common.exportExcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.BooleanUtils;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.handler.context.SheetWriteHandlerContext;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.thtf.office.common.util.SplitListUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author: chenjiaxiang
 * @create: 2022/10/18 16:21
 **/
@Slf4j
public class EasyExcelStyleUtils {

    /**
     * 生成exl文档 保存至本地
     *
     * @param tList    数据集合
     * @param t        映射对象
     * @param fileName 文件保存路径及文件名称
     * @param <T>      范型
     */
    public static <T> void customHandlerWrite(List<T> tList, Class<T> t, String fileName) {
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        EasyExcelFactory.write(fileName, t)
                //文件样式
                .registerWriteHandler(new CustomSheetWriteHandler())
//                .registerWriteHandler(new CustomSheetWriteHandler1())
//                .registerWriteHandler(new CustomCellWriteHandler())
                .sheet("sheet").doWrite(tList);
    }


    /**
     * 直接get请求下载excel
     *
     * @param tClass  对象
     * @param tList   数据集合
     * @param name    文件名称-只是名称
     * @param integer 判断样式字符，可自己去设置样式在 chooseStyle() 方法中
     */
    public <T> void customHandlerWrite(Class<T> tClass, List<T> tList, String name, HttpServletResponse response, Integer integer) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyExcel没有关系
        String fileName = null;
        try {
            fileName = URLEncoder.encode(name, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            //根据传输的integer选择自己需要的样式，样式按照自己需求来写
            chooseStyle(tClass, tList, response, integer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 创建exl及获取exl的InputStream流 仅做事例
     *
     * @param tList   数据集合
     * @param t       对象
     * @param integer 判断样式字符，可按需求设置样式，在createExl()方法中
     * @return 流
     */
    public static <T> InputStream customHandlerWrite(List<T> tList, Class<T> t, Integer integer) {
        ExcelWriter exl;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        OutputStream outputStream = new ByteArrayOutputStream();
        try {
            //模版方法，按需求创建exl格式及创建exl
            exl = createExl(tList, t, os, outputStream, integer);
            //将创建的exl转为输入流
            return downLoadStr(tList, exl, os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * 下载exl 可根据自己的需求样式来判断更改
     *
     * @param tClass  对象
     * @param tList   数据集合
     * @param integer 判断
     */
    private static <T> void chooseStyle(Class<T> tClass, List<T> tList, HttpServletResponse response, Integer integer) {
        try {
            if (integer == 0) {
                EasyExcel.write(response.getOutputStream(), tClass)
                        //文件样式
                        .registerWriteHandler(new CustomSheetWriteHandler())
                        .registerWriteHandler(new CustomSheetWriteHandler1())
                        .sheet("模板").doWrite(tList);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 生成的exl文件转为InputStream流
     *
     * @param list        数据集合
     * @param excelWriter exl
     * @param os          输出流
     * @return 流
     */
    public static <T> InputStream downLoadStr(List<T> list, ExcelWriter excelWriter, ByteArrayOutputStream os) {
        // 单个sheet的容量
        List<? extends List<?>> lists = SplitListUtil.split(list, 1000);
        //转换流
        ExcelWriterSheetBuilder excelWriterSheetBuilder;
        WriteSheet writeSheet;
        for (int i = 1; i <= lists.size(); ++i) {
            excelWriterSheetBuilder = new ExcelWriterSheetBuilder(excelWriter);
            excelWriterSheetBuilder.sheetNo(i).sheetName("sheet" + i);
            writeSheet = excelWriterSheetBuilder.build();
            excelWriter.write(lists.get(i - 1), writeSheet);
        }
        // 必须要finish才会写入，不finish只会创建empty的文件
        excelWriter.finish();
        byte[] content = os.toByteArray();
        // 返回流文件
        return new ByteArrayInputStream(content);
    }


    /**
     * 创建exl文件，按照自己的需求创建exl格式
     *
     * @param list         数据集合
     * @param clazz        对象
     * @param os           字节流
     * @param outputStream 输出流
     * @param integer      判断样式
     * @return exl
     */
    private static <T> ExcelWriter createExl(List<T> list, Class<T> clazz, ByteArrayOutputStream os, OutputStream outputStream, Integer integer) {
        ExcelWriter excelWriter = null;
        if (integer == 0) {
            // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
            EasyExcelFactory.write(outputStream, clazz)
                    .registerWriteHandler(new CustomSheetWriteHandler())
                    .registerWriteHandler(new CustomSheetWriteHandler1())
                    .sheet("sheet").doWrite(list);
            //浏览器访问url直接下载文件的方式
            excelWriter = EasyExcelFactory.write(os, clazz)
                    .registerWriteHandler(new CustomSheetWriteHandler())
                    .registerWriteHandler(new CustomSheetWriteHandler1())
                    .build();
        }
        return excelWriter;
    }

    /**
     * 自定义拦截器.对第一列第一行和第二行的数据新增下拉框，显示 我试试 我试试啊
     */
    @Slf4j
    public static class CustomSheetWriteHandler implements SheetWriteHandler {
        @Override
        public void afterSheetCreate(SheetWriteHandlerContext context) {
            log.info("第{}个Sheet写入成功。", context.getWriteSheetHolder().getSheetNo());
            Integer lastRowIndex = context.getWriteSheetHolder().getLastRowIndex();
            log.info("追后一条记录的行数：{}",lastRowIndex);

            // 区间设置 给第一列的 第一行到第9行 加下拉框
            CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, 9, 0, 0);
            DataValidationHelper helper = context.getWriteSheetHolder().getSheet().getDataValidationHelper();
            DataValidationConstraint constraint = helper.createExplicitListConstraint(new String[]{"我试试", "我试试啊"});
            DataValidation dataValidation = helper.createValidation(constraint, cellRangeAddressList);
            context.getWriteSheetHolder().getSheet().addValidationData(dataValidation);
        }
    }

    /**
     * 自定义拦截器.对第一列第一行和第二行的数据新增下拉框，显示 我试试 我试试啊
     */
    @Slf4j
    public static class CustomSheetWriteHandler1 implements SheetWriteHandler {
        @Override
        public void afterSheetCreate(SheetWriteHandlerContext context) {
            log.info("第{}个Sheet写入成功。", context.getWriteSheetHolder().getSheetNo());
            // 区间设置 给第2、3列的 第2行到100行加下拉框
            CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(2, 100, 2, 3);
            DataValidationHelper helper = context.getWriteSheetHolder().getSheet().getDataValidationHelper();
            DataValidationConstraint constraint = helper.createExplicitListConstraint(new String[]{"我试试1111", "我试试2222啊"});
            DataValidation dataValidation = helper.createValidation(constraint, cellRangeAddressList);
            context.getWriteSheetHolder().getSheet().addValidationData(dataValidation);
        }

    }

    @Slf4j
    public static class CustomSheetWriteHandler2 implements SheetWriteHandler {
        @Override
        public void afterSheetCreate(SheetWriteHandlerContext context) {

//        }
//
//        @Override
//        public void afterSheetDispose(RowWriteHandlerContext context) {
//
//        }
//
//        @Override
//        public void after(SheetWriteHandlerContext context) {
            Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
            Sheet sheet = workbook.getSheetAt(0);
            System.out.println(sheet.getRow(0).getCell(0).getStringCellValue());
            Row row1 = sheet.createRow(0);
            row1.setHeight((short) 500);

            Cell cell = row1.createCell(0);

            //设置单元格内容

            cell.setCellValue("附件1");

            //设置标题

            Row row2 = sheet.createRow(1);

            row2.setHeight((short) 800);

            Cell cell1 = row2.createCell(0);

            cell1.setCellValue("高博医院公车信息登记表");

            CellStyle cellStyle = workbook.createCellStyle();

            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            Font font = workbook.createFont();

            font.setBold(true);

            font.setFontHeight((short) 400);

            cellStyle.setFont(font);

            cell1.setCellStyle(cellStyle);

            sheet.addMergedRegionUnsafe(new CellRangeAddress(1, 1, 0, 12));
            //设置填表日期,填报人,联系方式

            Row row3 = sheet.createRow(2);

            row3.setHeight((short) 500);

            row3.createCell(0).setCellValue("填表日期：");

            row3.createCell(10).setCellValue("填表人：");

            row3.createCell(12).setCellValue("联系方式：");
        }
    }

    /**
     * 自定义拦截器，给表头或者 规定的 坐标加超链接，双击进入
     */
    public static class CustomCellWriteHandler implements CellWriteHandler {
        @Override
        public void afterCellDispose(CellWriteHandlerContext context) {
            Cell cell = context.getCell();
            // 这里可以对cell进行任何操作
            log.info("第{}行，第{}列写入完成。", cell.getRowIndex(), cell.getColumnIndex());
            //BooleanUtils.isTrue(context.getHead()) &&  为 给请求头加超链接且 必须是第3行第2列
            if (BooleanUtils.isTrue(context.getHead()) && cell.getColumnIndex() == 2 && cell.getRowIndex() == 3) {
                CreationHelper createHelper = context.getWriteSheetHolder().getSheet().getWorkbook().getCreationHelper();
                Hyperlink hyperlink = createHelper.createHyperlink(HyperlinkType.URL);
                hyperlink.setAddress("https://www.baidu.com");
                cell.setHyperlink(hyperlink);
            }
        }
    }
}