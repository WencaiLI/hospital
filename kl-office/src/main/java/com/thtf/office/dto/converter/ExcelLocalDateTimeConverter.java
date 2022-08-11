package com.thtf.office.dto.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.*;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Author: liwencai
 * @Date: 2022/7/31 21:29
 * @Description: EasyExcel的LocalDateTime自定义转换器
 */
@Slf4j
public class ExcelLocalDateTimeConverter implements Converter<String> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public String convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        CellDataTypeEnum type = cellData.getType();
        if (CellDataTypeEnum.STRING == type){
            return cellData.getStringValue();
        }
        if(CellDataTypeEnum.NUMBER == type){
            return cellData.getNumberValue().toString();
        }
        return null;
    }

    @Override
    public WriteCellData<?> convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if(StringUtils.isBlank(value)){
            return new WriteCellData<>();
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dateTimeFormatter.format(dateTimeFormatter.parse(value));
        return new WriteCellData<>(dateTimeFormatter.format(dateTimeFormatter.parse(value)));
    }
}
