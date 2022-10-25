package com.thtf.environment.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/10/25 14:57
 * @Description:
 */
@Data
public class EnvMonitorItemParamVO {

    private String itemTypeCode; // 设备类别编码

    private String buildingCodes; // 建筑编码

    private String areaCode; // 区域编码

    private Integer onlineStatus; // 在线状态

    private Integer status; // 设备状态 正常 故障 报警

    private String keyword; // 关键词 监测设备名称及编号的模糊检索

    private Integer pageNumber; // 页号

    private Integer pageSize; // 页大小
}
