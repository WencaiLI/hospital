package com.thtf.broadcast.service.impl;

import com.thtf.broadcast.dto.DisplayInfoDTO;
import com.thtf.broadcast.service.BroadcastService;
import com.thtf.common.feign.AdminAPI;
import com.thtf.common.feign.AlarmAPI;
import com.thtf.common.feign.ItemAPI;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/7 13:42
 * @Description:
 */
@Service
public class BroadcastServiceImpl implements BroadcastService {
    @Resource
    AdminAPI adminAPI;
    @Resource
    ItemAPI itemAPI;
    @Resource
    AlarmAPI alarmAPI;

    /**
     * @Author: liwencai
     * @Description: 前端数据展示
     * @Date: 2022/10/7
     * @Param sysCode: 子系统编码
     * @Param itemType: 设备类别编码
     * @return: java.util.List<com.thtf.broadcast.dto.DisplayInfoDTO>
     */
    @Override
    public List<DisplayInfoDTO> displayInfo(String sysCode, String itemType) {
        return null;
    }
}
