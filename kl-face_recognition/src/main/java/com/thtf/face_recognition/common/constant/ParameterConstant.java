package com.thtf.face_recognition.common.constant;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/12/6 11:21
 * @Description:
 */
@Data
public class ParameterConstant {
    /**
     * 在线状态编码
     */
    public static final String FACE_RECOGNITION_ONLINE= "OnlineStatus";

    /**
     * 在线状态的值
     */
    public static final String FACE_RECOGNITION_ONLINE_VALUE = "1";

    /**
     * 离线状态的值
     */
    public static final String FACE_RECOGNITION_OFFLINE_VALUE = "0";

    /**
     * 报警状态编码
     */
    public static final String FACE_RECOGNITION_ALARM = "Alarm";

    /**
     * 方位
     */
    public static final String FACE_RECOGNITION_Position = "Position";
}
