package com.thtf.office.dto.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * @Author: liwencai
 * @Date: 2022/8/10 14:50
 * @Description: EasyExcel的BigDecimal自定义转换器
 */
@Slf4j
public class ExcelBigDecimalConverter implements Converter<String> {
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
        if(CellDataTypeEnum.NUMBER == type){
            return cellData.getNumberValue().toString();
        }
        if(CellDataTypeEnum.STRING == type){
            return cellData.getStringValue();
        }
        log.error("目标格式只能使用文本或者数字格式，目前格式为:"+type+",暂时无法解析");
        return null;
    }

    @Override
    public WriteCellData<?> convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new WriteCellData<>(new BigDecimal(value));
    }
}
