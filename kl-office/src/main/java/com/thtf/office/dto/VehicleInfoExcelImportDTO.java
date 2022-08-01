package com.thtf.office.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.thtf.office.dto.converter.LocalDateTimeConverter;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Auther: liwencai
 * @Date: 2022/7/30 21:59
 * @Description: 公车导入Excel的DataTransformObject
 */
@Data
public class VehicleInfoExcelImportDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 车牌号
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "车牌号",index = 0)
    private String carNumber;

    /**
     * 所属类别名称
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "所属类别",index = 1)
    private String vehicleCategoryName;

    /**
     * 厂牌型号
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "厂牌型号",index = 2)
    private String model;

    /**
     * 发动机号
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "发动机号",index = 3)
    private String engineNumber;

    /**
     * 车架号
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "车架号",index = 4)
    private String frameNumber;

    /**
     * 车身颜色
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "车身颜色",index = 5)
    private String color;

    /**
     * 车辆照片名称
     */
    private String carImage;

    /**
     * 车辆照片url
     */
    private String carImageUrl;

    /**
     * 车辆行驶本照片名称
     */
    private String drivingBookImage;

    /**
     * 车辆行驶本照片url
     */
    private String drivingBookImageUrl;

    /**
     * 经销商
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "经销商",index = 6)
    private String distributor;

    /**
     * 出厂日期
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "出厂日期",index = 7,converter = LocalDateTimeConverter.class)
    private LocalDateTime outDate;

    /**
     * 购买日期
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "购买日期",index = 8,converter = LocalDateTimeConverter.class)
    private LocalDateTime buyDate;

    /**
     * 购买价格
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "购买价格",index = 9)
    private BigDecimal price;

    /**
     * 保险说明
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "保险说明",index = 10)
    private String insurance;

    /**
     * 维保说明
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "维保说明",index = 11)
    private String maintenance;

    /**
     * 描述
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "公车描述",index = 12)
    private String description;
}
