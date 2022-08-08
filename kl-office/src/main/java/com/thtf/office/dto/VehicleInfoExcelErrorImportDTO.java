package com.thtf.office.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.thtf.office.dto.converter.LocalDateTimeConverter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: liwencai
 * @Date: 2022/8/8 20:16
 * @Description:
 */
@Data
public class VehicleInfoExcelErrorImportDTO {

    /**
     * 车牌号
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "行号",index = 0)
    private String rowNumber;
    /**
     * 车牌号
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "车牌号",index = 1)
    private String carNumber;

    /**
     * 所属类别名称
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "所属类别",index = 2)
    private String vehicleCategoryName;

    /**
     * 厂牌型号
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "厂牌型号",index = 3)
    private String model;

    /**
     * 发动机号
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "发动机号",index = 4)
    private String engineNumber;

    /**
     * 车架号
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "车架号",index = 5)
    private String frameNumber;

    /**
     * 车身颜色
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "车身颜色",index = 6)
    private String color;

    /**
     * 经销商
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "经销商",index = 7)
    private String distributor;

    /**
     * 出厂日期
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "出厂日期",index = 8,converter = LocalDateTimeConverter.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime outDate;

    /**
     * 购买日期
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "购买日期",index = 9,converter = LocalDateTimeConverter.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime buyDate;

    /**
     * 购买价格
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "购买价格",index = 10)
    private BigDecimal price;

    /**
     * 保险说明
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "保险说明",index = 11)
    private String insurance;

    /**
     * 维保说明
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "维保说明",index = 12)
    private String maintenance;

    /**
     * 描述
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "公车描述",index = 13)
    private String description;

    /**
     * 描述
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "错误信息",index = 14)
    private String errorInfo;
}
