package com.thtf.office.common.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.data.ReadCellData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: liwencai
 * @Date: 2022/8/8 15:52
 * @Description:
 */
public class EasyExcelErrorFixUtil {

    /**
     * 收集导入时的错误数据
     * @param exception
     * @param context 出现异常的内容
     * @param data 将错误数据转换格式后赋值给空集合
     * @param size title的长度
     */
    public static void setErrorData(Exception exception, AnalysisContext context, List<List<String>> data, int size) {
        if (exception instanceof ExcelDataConvertException) {

            ExcelDataConvertException convertException = (ExcelDataConvertException) exception;

            int row = convertException.getRowIndex();
            int column = convertException.getColumnIndex();

            Map<Integer, Cell> cellMapResult = context.readRowHolder().getCellMap();

            List<String> dataList = new ArrayList<>();
            dataList.add(Integer.toString(row));
            for (int i = 0; i < size; i ++) {

                ReadCellData cell = (ReadCellData) cellMapResult.get(i);
                if(cell == null){
                    dataList.add(null);
                }else if(column == i){
                    dataList.add("{错误信息}" + exception);
                }else {
                    dataList.add(cell.getStringValue());
                }
            }
            data.add(dataList);
            System.out.println(data.toString());
        }
    }

    /**
     * 收集导入时Excel的title
     * @param headMap 传入需要处理的title map集合
     * @param head 赋值给空集合
     */
    public static void setExcelErrorHead(Map<Integer, String> headMap, List<List<String>> head) {
        List<String> errorTips = new ArrayList<>();
        errorTips.add("错误数据行");
        head.add(errorTips);
        for(Map.Entry<Integer, String>  entry: headMap.entrySet()) {
            List<String> errorHead = new ArrayList<>();
            errorHead.add(entry.getValue());
            head.add(errorHead);
        }
    }
}

