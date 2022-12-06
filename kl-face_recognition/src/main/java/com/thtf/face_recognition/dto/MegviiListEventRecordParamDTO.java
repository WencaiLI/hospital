package com.thtf.face_recognition.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/12/6 22:02
 * @Description:
 */
@Data
public class MegviiListEventRecordParamDTO {

    /**
     * 结束时间戳 必须
     */
    private Long startTime;

    /**
     * 结束时间戳 必须
     */
    private Long endTime;

    /**
     * 设备uuid集合
     */
    private List<String> deviceUuids;

    /**
     * 事件类型id，具体可选值，参看下面解释
     */
    private Long eventTypeId;

    /**
     * 事件等级id：1-高,2-中,3-低
     */
    private Long eventLevelId;

    /**
     * 事件状:0-处理,1-已处理
     */
    private Integer status;

    /**
     * 当前页码默认1
     */
    private Integer pageNum;

    /**
     * 当前页大小
     */
    private Integer pageSize;
}
