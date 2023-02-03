package com.thtf.face_recognition.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/23 10:56
 * @Description:
 */
@Data
public class PageInfoVO<T> implements Serializable {
    public static final int DEFAULT_NAVIGATE_PAGES = 8;

    /**
     * 当前页
     */
    private int pageNum;

    /**
     * 每页的数量
     */
    private int pageSize;

    /**
     * 当前页的数量
     */
    private int size;


    /**
     * 当前页面第一个元素在数据库中的行号
     */
    private long startRow;

    /**
     * 当前页面最后一个元素在数据库中的行号
     */
    private long endRow;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 前一页
     */
    private int prePage;

    /**
     * 下一页
     */
    private int nextPage;

    /**
     * 是否为第一页
     */
    private boolean isFirstPage = false;

    /**
     * 是否为最后一页
     */
    private boolean isLastPage = false;

    /**
     * 是否有前一页
     */
    private boolean hasPreviousPage = false;

    /**
     * 是否有下一页
     */
    private boolean hasNextPage = false;

    /**
     * 导航页码数
     */
    private int navigatePages;

    /**
     * 所有导航页号
     */
    private int[] navigatepageNums;

    /**
     * 导航条上的第一页
     */
    private int navigateFirstPage;

    /**
     * 导航条上的最后一页
     */
    private int navigateLastPage;

    /**
     *
     */
    private long total;

    /**
     * 结果集
     */
    private List<T> list;
}
