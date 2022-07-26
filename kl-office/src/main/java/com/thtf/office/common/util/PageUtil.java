package com.thtf.office.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.thtf.office.common.response.PageResult;

import java.util.List;

/**
 * 构建PageResult对象工具类
 *
 * @author ligh
 * @date 2021-05-12
 */
public class PageUtil<T> {

    public static <T> PageResult buildPageResult(IPage<T> queryResult, int pageSize) {
        if (queryResult == null) {
            return new PageResult<>();
        }
        List<T> result = queryResult.getRecords();
        long total = queryResult.getTotal();
        long current = queryResult.getCurrent();
        long pageNumber;
        if (total % pageSize == 0) {
            pageNumber = total / pageSize;
        } else {
            pageNumber = total / pageSize + 1;
        }
        return new PageResult<>(result, total, current, pageNumber);
    }

}
