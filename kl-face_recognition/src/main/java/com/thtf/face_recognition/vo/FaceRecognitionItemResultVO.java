package com.thtf.face_recognition.vo;

import com.thtf.common.dto.itemserver.ListVideoItemResultDTO;
import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/11/7 10:14
 * @Description:
 */
@Data
public class FaceRecognitionItemResultVO {

    /**
     * 设备编码
     */
    private String itemCode;

    /**
     * 设备名称
     */
    private String itemName;

    /**
     * 设备描述
     */
    private String itemDescription;

    /**
     * 设备类别
     */
    private String itemTypeName;

    /**
     * 在线状态参数编码
     */
    private String onlineParameterCode;

    /**
     * 在线状态参数值
     */
    private String onlineParameterValue;

    /* ******** 摄像机相关信息 ******** */

    /**
     * 摄像机用户名
     */
    private String videoUsername;

    /**
     * 摄像机密码
     */
    private String videoPassword;

    /**
     * ip地址
     */
    private String ipAddress;

    /**
     * 方位
     */
    private String position;

    /**
     * 通道号
     */
    private String channelNum;

    /**
     * 报警参数编码
     */
    private String alarmParameterCode;

    /**
     * 报警参数值
     */
    private String alarmParameterValue;

    /**
     * 相机视角
     */
    private List<Integer> eye;

    /**
     * 人眼视角
     */
    private List<Integer> center;

//    /**
//     * 摄像设备
//     */
//    private ListVideoItemResultDTO videoInfo;
}