package com.thtf.office.common.exportExcel;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *  @className importVehicleToList
 *  @Descripton
 *  @Author Deng
 *  @Date 2021/1/1314:54
 **/
public class ExcelVehicleUtils {
    public static List<String[]> importExtendToList(MultipartFile file, String endName) throws IOException {
        List<String[]> list = new ArrayList<String[]>();
        InputStream input = file.getInputStream();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (endName.endsWith("xls")) {
            try {
                HSSFWorkbook wb = new HSSFWorkbook(input);
                for (int i = 0; i < 1; i++) {
                    HSSFSheet sheet = wb.getSheetAt(i);
                    for (int rowNum = 7; rowNum <= sheet.getLastRowNum(); rowNum++) {
                        HSSFRow row = sheet.getRow(rowNum);

                        String[] strs = new String[50];
                        boolean type = true;
                        int num = 0;
                        for (int j = 0; j < 12; j++) {
                            HSSFCell cell = row.getCell(j);
                            if (cell != null && cell.getCellType() != HSSFCell.CELL_TYPE_BLANK) {
                                if (cell.getCellType() == 0) {
                                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                        Date date = cell.getDateCellValue();
                                        strs[j] = sdf.format(date);
                                    } else {
                                        cell.setCellType(1);
                                        strs[j] = cell.getStringCellValue();
                                    }
                                } else if (cell.getCellType() == 1) {
                                    strs[j] = cell.getStringCellValue();
                                } else {
                                    strs[j] = getValueHSSF(cell);
                                }
                                type = true;
                            } else {
                                type = false;
                                num++;
                                strs[j] = "";
                            }
                        }
                        if(num < 14){
                            list.add(strs);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (input == null) {
                        return list;
                    }
                    input.close();
                } catch (IOException a) {
                    a.printStackTrace();
                }
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                if (input == null) {
                    return list;
                }
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (endName.endsWith("xlsx")) {
            try {
                XSSFWorkbook wb = new XSSFWorkbook(input);
                for (int i = 0; i < 1; i++) {
                    XSSFSheet sheet = wb.getSheetAt(i);
                    for (int rowNum = 7; rowNum <= sheet.getLastRowNum(); rowNum++) {
                        XSSFRow row = sheet.getRow(rowNum);
                        String[] strs = new String[50];
                        boolean type = true;
                        int num = 0;
                        for (int cellNum = 0; cellNum < 12; cellNum++) {
                            XSSFCell cell = row.getCell(cellNum);
                            if (cell != null && cell.getCellType() != HSSFCell.CELL_TYPE_BLANK) {
                                cell.setCellType(Cell.CELL_TYPE_STRING);
                                strs[cellNum] = cell.getStringCellValue();
                                type = true;
                            } else {
                                type = false;
                                num++;
                            }
                        }
                        if(num < 14){
                            list.add(strs);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    input.close();
                } catch (IOException a) {
                    a.printStackTrace();
                }
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    private static String getValueHSSF(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == 4) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        }
        if (hssfCell.getCellType() == 0) {
            return String.valueOf(hssfCell.getNumericCellValue());
        }
        return String.valueOf(hssfCell.getStringCellValue());
    }


    public static HorizontalCellStyleStrategy getStyleStrategy(){
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为灰色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short)12);
        // 字体样式
        headWriteFont.setFontName("微软雅黑");
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
        contentWriteFont.setFontName("微软雅黑");
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

}
