package com.thtf.environment.service;

import com.thtf.environment.dto.ItemInfoOfLargeScreenDTO;
import com.thtf.environment.dto.ItemPlayInfoDTO;
import com.thtf.environment.dto.PageInfoVO;

import java.util.List;
import java.util.Map;

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
    PageInfoVO getLargeScreenInfo(Map<String, Object> paramMap);

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
    PageInfoVO getLargeScreenAlarmInfo(String sysCode,String buildingCodes, String areaCode, String keyword, Integer pageNumber, Integer pageSize);

    /**
     * @Author: liwencai
     * @Description: 远程控制
     * @Date: 2022/11/3
     * @Param: sysCode: 
     * @Param: itemCodes: 
     * @Return: java.lang.Boolean
     */
    Boolean remoteSwitch(String sysCode, String itemCodes);

    /**
     * @Author: liwencai
     * @Description: 
     * @Date: 2022/11/3
     * @Param: param: 
     * @Return: java.lang.Boolean
     */
    Boolean insertPlayOrder(ItemPlayInfoDTO param);

    /**
     * @Author: liwencai
     * @Description: 获取信息发布大屏内容
     * @Date: 2022/11/2
     * @Param: sysCode: 子系统编码
     * @Param: buildingCodes: 建筑编码集
     * @Param: areaCode: 区域编码
     * @Param: itemCodes: 设备编码集
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.environment.dto.ItemPlayInfoDTO>>
     */
    List<ItemPlayInfoDTO> listLargeScreenContent(String sysCode, String buildingCodes, String areaCode, String itemCodes);

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/30
     * @Param sysCode:
     * @Param itemCodes:
     * @return: com.thtf.environment.dto.InfoPublishPointDTO
     */
    ItemInfoOfLargeScreenDTO getMonitorPoint(String sysCode, String itemCode);
}
