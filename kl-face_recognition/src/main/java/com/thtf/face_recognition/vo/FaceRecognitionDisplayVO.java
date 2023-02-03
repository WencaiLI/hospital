package com.thtf.face_recognition.vo;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/11/7 10:06
 * @Description:
 */
@Data
public class FaceRecognitionDisplayVO {
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
    private Integer alarmNum;

    /**
     * 报警总数
     */
    private Integer faultNum;

    /**
     * 离线设备总数
     */
    // private Integer offlineNum;
}
