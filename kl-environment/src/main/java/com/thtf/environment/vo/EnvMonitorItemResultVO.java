package com.thtf.environment.vo;

import com.thtf.common.entity.itemserver.TblItemParameter;
import com.thtf.environment.dto.VideoInfoDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/25 14:55
 * @Description:
 */
@Data
public class EnvMonitorItemResultVO {
    private String itemCode; // 设备编码
    private String itemName; // 设备名称
    private String areaCode; // 区域编码
    private String areaName; // 区域名称
//    private String onlineParameterCode; // 在线状态参数编码
//    private String onlineStatus; // 在线状态
//    private String alarmParameterCode; // 在线状态参数编码
//    private String alarmStatus; // 在线状态 对应alarmCategory
//    private List<VideoInfoDTO> videoList; // 摄像机编码信息
//    private Object dataCollectionValue; // 数据采集值
//    private LocalDateTime dataCollectionTime; // 数据采集时间
    private List<TblItemParameter> parameterList;
    private List<Integer> eye; // 视角定位
    private List<Integer> center; // 视角定位

}
