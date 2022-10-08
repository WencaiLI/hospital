package com.thtf.elevator.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/23 10:56
 * @Description:
 */
@Data
public class PageInfoVO implements Serializable {
    public static final int DEFAULT_NAVIGATE_PAGES = 8;

    private int pageNum;  //当前页

    private int pageSize; //每页的数量

    private int size; //当前页的数量

    //由于startRow和endRow不常用，这里说个具体的用法
    //可以在页面中"显示startRow到endRow 共size条数据"

    private long startRow; //当前页面第一个元素在数据库中的行号

    private long endRow; //当前页面最后一个元素在数据库中的行号

    private int pages; //总页数

    private int prePage; //前一页

    private int nextPage;  //下一页

    private boolean isFirstPage = false;  //是否为第一页

    private boolean isLastPage = false; //是否为最后一页

    private boolean hasPreviousPage = false; //是否有前一页

    private boolean hasNextPage = false;  //是否有下一页

    private int navigatePages; //导航页码数

    private int[] navigatepageNums;  //所有导航页号

    private int navigateFirstPage;  //导航条上的第一页

    private int navigateLastPage;  //导航条上的最后一页

    private List list;  // 结果集
}
