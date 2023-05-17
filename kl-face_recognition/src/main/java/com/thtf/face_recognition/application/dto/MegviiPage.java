package com.thtf.face_recognition.application.dto;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/12/7 09:54
 * @Description:
 */
@Data
public class MegviiPage<T> {
    /**
     * 页号
     */
    private Integer pageNum;

    /**
     * 页大小
     */
    private Integer pageSize;

    /**
     * 总数
     */
    private Long total;

    /**
     * 数据
     */
    private List<T> list;
}
