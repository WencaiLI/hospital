package com.thtf.face_recognition.vo.app;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2023/2/17 19:17
 * @Description:
 */
@Data
public class AppFaceRecognitionDisplayVO {
    /**
     * 设备总数
     */
    private Integer itemNum;

    /**
     * 在线总数
     */
    private Integer onlineNum;

    /**
     * 报警总数
     */
    private Integer alarmItemNum;

    /**
     * 故障总数
     */
    private Integer faultItemNum;

    private Long alarmNum;

    private Long faultNum;

    /**
     * 离线设备总数
     */
    // private Integer offlineNum;
}
