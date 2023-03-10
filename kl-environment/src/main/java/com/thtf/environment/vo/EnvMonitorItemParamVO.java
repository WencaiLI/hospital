package com.thtf.environment.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/10/25 14:57
 * @Description:
 */
@Data
public class EnvMonitorItemParamVO {
    /**
     * 子系统编码
     */
    private String sysCode;
    /**
     * 设备类别编码
     */
    private String itemTypeCode;

    /**
     * 建筑编码
     */
    private String buildingCodes;

    /**
     * 区域编码
     */
    private String areaCode;

    /**
     * 报警类别
     */
    private Integer alarmCategory;


//    private Integer onlineStatus; // 在线状态
//
//    private Integer status; // 设备状态 正常 故障 报警

    /**
     * 关键词 监测设备名称及编号的模糊检索
     */
    private String keyword;

    /**
     * 页号
     */
    private Integer pageNumber;

    /**
     * 页大小
     */
    private Integer pageSize;
}
