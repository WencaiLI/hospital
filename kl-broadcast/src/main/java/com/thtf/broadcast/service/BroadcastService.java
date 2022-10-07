package com.thtf.broadcast.service;

import com.thtf.broadcast.dto.DisplayInfoDTO;

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
     * @return: java.util.List<com.thtf.broadcast.dto.DisplayInfoDTO>
     */
    List<DisplayInfoDTO> displayInfo(String sysCode, String itemType);



}
