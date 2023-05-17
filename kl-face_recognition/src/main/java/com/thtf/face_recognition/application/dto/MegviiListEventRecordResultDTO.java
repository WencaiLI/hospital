package com.thtf.face_recognition.application.dto;

/**
 * @Author: liwencai
 * @Date: 2022/12/6 22:10
 * @Description:
 */

import lombok.Data;

@Data
public class MegviiListEventRecordResultDTO {

    /**
     * 事件记uuid
     */
    private String uuid;

    /**
     * 抓拍图uri
     */
    private String captureImageUrl;

    /**
     * 底库图
     */
    private String groupImageUrl;

    /**
     * 全景图
     */
    private String fullImageUrl;

    /**
     * 对比分
     */
    private Double score;

    /**
     * 事件类型名
     */
    private String eventTypeName;

    /**
     * 事件类型id
     */
    private Long eventTypeId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 事件等级id
     */
    private Long eventLevelId;

    /**
     * 事件等级名称
     */
    private String eventLevelName;

    /**
     * 事件等级颜色
     */
    private String eventLevelColor;

    /**
     * 事件发生时间戳
     */
    private Long timestamp;

    /**
     * 事件状：0-处理，1-处理
     */
    private Integer status;

    /**
     * 事件处理意见
     */
    private String remark;

    /**
     * 事件处理人
     */
    private String dealUser;

    /**
     * 事件处理时间戳
     */
    private Long dealTime;

    /**
     * 扩展信息，JSON格式
     */
    private String ext;
}
