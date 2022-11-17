package com.thtf.office.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.thtf.office.dto.converter.ExcelBigDecimalConverter;
import com.thtf.office.dto.converter.ExcelLocalDateTimeConverter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Author: liwencai
 * @Date: 2022/7/30 21:59
 * @Description: 公车导入Excel的DataTransformObject,数据类型全部为String类型方便做统一的数据验证
 */
@Data
public class VehicleInfoExcelImportDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String TITTLE = "高博医院公车登记表";

    /**
     * 车牌号
     */
    @ColumnWidth(20)
    //@ExcelProperty(value = {TITTLE,"车牌号"},index = 0)
    @ExcelProperty(value = "车牌号",index = 0)
    private String carNumber;

    /**
     * 所属类别名称
     */
    @ColumnWidth(20)
    //@ExcelProperty(value = {TITTLE,"所属类别"},index = 1)
    @ExcelProperty(value = "所属类别",index = 1)
    private String vehicleCategoryName;

    /**
     * 厂牌型号
     */
    @ColumnWidth(20)
    //@ExcelProperty(value = {TITTLE,"厂牌型号"},index = 2)
    @ExcelProperty(value = "厂牌型号",index = 2)
    private String model;

    /**
     * 发动机号
     */
    @ColumnWidth(20)
    //@ExcelProperty(value = {TITTLE,"发动机号"},index = 3)
    @ExcelProperty(value = "发动机号",index = 3)
    private String engineNumber;

    /**
     * 车架号
     */
    @ColumnWidth(20)
    //@ExcelProperty(value = {TITTLE,"车架号"},index = 4)
    @ExcelProperty(value = "车架号",index = 4)
    private String frameNumber;

    /**
     * 车身颜色
     */
    @ColumnWidth(20)
    //@ExcelProperty(value = {TITTLE,"车身颜色"},index = 5)
    @ExcelProperty(value = "车身颜色",index = 5)
    private String color;

    /**
     * 经销商
     */
    @ColumnWidth(20)
    //@ExcelProperty(value = {TITTLE,"经销商"},index = 6)
    @ExcelProperty(value = "经销商",index = 6)
    private String distributor;

    /**
     * 出厂日期
     */
    @ColumnWidth(20)
    //@ExcelProperty(value = {TITTLE,"出厂日期"},index = 7)
    @ExcelProperty(value = "出厂日期",index = 7,converter = ExcelLocalDateTimeConverter.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String outDate;

    /**
     * 购买日期
     */
    @ColumnWidth(20)
    //@ExcelProperty(value = {TITTLE,"购买日期"},index = 8)
    @ExcelProperty(value = "购买日期",index = 8,converter = ExcelLocalDateTimeConverter.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String buyDate;

    /**
     * 购买价格
     */
    @ColumnWidth(20)
    //@ExcelProperty(value = {TITTLE,"购买价格"},index = 9)
    @ExcelProperty(value = "购买价格",index = 9,converter = ExcelBigDecimalConverter.class)
    private String price;

    /**
     * 保险说明
     */
    @ColumnWidth(20)
    //@ExcelProperty(value = {TITTLE,"保险说明"},index = 10)
    @ExcelProperty(value = "保险说明",index = 10)
    private String insurance;

    /**
     * 维保说明
     */
    @ColumnWidth(20)
    //@ExcelProperty(value = {TITTLE,"维保说明"},index = 11)
    @ExcelProperty(value = "维保说明",index = 11)
    private String maintenance;

    /**
     * 描述
     */
    @ColumnWidth(20)
    //@ExcelProperty(value = {TITTLE,"公车描述"},index = 12)
    @ExcelProperty(value = "公车描述",index = 12)
    private String description;
}
