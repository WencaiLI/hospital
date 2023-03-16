package com.thtf.environment.service;

import com.github.pagehelper.PageInfo;
import com.thtf.environment.dto.*;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/7 13:41
 * @Description:
 */
public interface BroadcastService {

    /**
     * @Author: liwencai
     * @Description: 前端数据展示
     * @Date: 2022/10/7
     * @Param sysCode: 子系统编码
     * @Param itemType: 设备类别编码
     * @return: com.thtf.broadcast.dto.DisplayInfoDTO
     */
    DisplayInfoDTO displayInfo(String sysCode, String buildingCodes, String areaCode);

    /**
     * @Author: liwencai 
     * @Description: 获取控制信息
     * @Date: 2022/10/7
     * @Param sysCode: 
     * @return: java.util.List<com.thtf.environment.dto.KeyValueDTO> 
     */
    List<KeyValueDTO> controlInfo(String sysCode);

    /**
     * @Author: liwencai
     * @Description: 获取设备信息
     * @Date: 2022/10/7
     * @Param keyword:
     * @Param sysCode:
     * @Param areaCode:
     * @Param pageNumber:
     * @Param pageSize:
     * @return: java.util.List<com.thtf.environment.dto.ItemInfoOfBroadcastDTO>
     */
    PageInfo<ItemInfoOfBroadcastDTO> getItemInfo(String sysCode, String buildingCodes, String areaCode, String runVale, String keyword, Integer pageNumber, Integer pageSize);

    /**
     * @Author: liwencai 
     * @Description:
     * @Date: 2022/10/7
     * @Param keyword: 
     * @Param sysCode:
     * @Param pageNumber:
     * @Param pageSize:
     * @return: com.thtf.environment.dto.PageInfoVO 
     */
    PageInfo<AlarmInfoOfBroadcastDTO> getAlarmInfo(String keyword, String sysCode, String buildingCodes, String areaCode, Integer pageNumber, Integer pageSize);

    /**
     * @Author: liwencai
     * @Description: 
     * @Date: 2022/11/3
     * @Param: itemCode: 
     * @Return: java.util.List<com.thtf.environment.dto.BroadcastContentInsertDTO>
     */
    List<BroadcastContentInsertDTO> getPublishContent(String itemCode);

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/3
     * @Param: param:
     * @Return: java.lang.Boolean
     */
    Boolean publishContent(BroadcastContentInsertDTO param);

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/12/1
     * @Param sysCode:
     * @Param itemCodes:
     * @return: com.thtf.environment.dto.ItemInfoOfLargeScreenDTO
     */
    ItemInfoOfBroadcastDTO getMonitorPoint(String sysCode, String itemCodes);
}
