package com.thtf.office.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页数据包装对象
 *
 * @author guola
 * @date 20202-06-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageSumResult<T> {

    private static final long serialVersionUID = -7857697536260073731L;

    /**
     * 记录列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页
     */
    private long current;

    /**
     * 总页数
     */
    private long pageNumber;

    /**
     * 未按条件过滤的总记录数 --todo guola
     */
    private long sum;
}
