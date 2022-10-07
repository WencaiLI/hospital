package com.thtf.environment.service;

import com.thtf.environment.dto.AlarmInfoOfLargeScreenDTO;
import com.thtf.environment.dto.ItemInfoOfLargeScreenDTO;
import com.thtf.environment.dto.PageInfoVO;

import java.util.List;

/**
 * @Auther: liwencai
 * @Date: 2022/9/21 17:22
 * @Description:
 */
public interface InfoPublishService {
    /**
     * @Author: liwencai
     * @Description: 查询大屏信息
     * @Date: 2022/9/22
     * @Param sysCode: 子系统编码
     * @Param areaCode: 区域编码
     * @Param keyword: 关键字
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: java.util.List<com.thtf.environment.dto.ItemInfoOfLargeScreenDTO>
     */
    PageInfoVO getLargeScreenInfo(String sysCode, String areaCode, String keyword, Integer pageNumber, Integer pageSize);

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/9/22
     * @Param sysCode: 子系统编码
     * @Param keyword: 关键字
     * @Param pageNumber: 页号
     * @Param pageSize: 页大小
     * @return: java.util.List<com.thtf.environment.dto.AlarmInfoOfLargeScreenDTO>
     */
    PageInfoVO getLargeScreenAlarmInfo(String sysCode, String keyword, Integer pageNumber, Integer pageSize);
}