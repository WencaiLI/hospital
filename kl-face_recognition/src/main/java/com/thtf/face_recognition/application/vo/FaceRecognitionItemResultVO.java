package com.thtf.face_recognition.application.vo;

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
     * 建筑编码
     */
    private String buildingCode;

    /**
     * 区域名称
     */
    private String buildingAreaName;

    /**
     * 报警类型
     */
    private String alarmCategory;

    /**
     * 区域编码
     */
    private String areaCode;

    /**
     * 设备描述
     */
    private String description;

    /**
     * 设备类别
     */
    private String itemTypeName;

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
     * 通道号
     */
    private String channelNum;

    /* ****** 设备参数信息 ********** */
    /**
     * 在线状态参数编码
     */
    private String onlineParameterCode;

    /**
     * 在线状态参数值
     */
    private String onlineValue;

    /**
     * 方位
     */
    private String positionParameterCode;

    /**
     * 方位值
     */
    private String positionValue;

    /**
     * 报警参数编码
     */
    private String alarmParameterCode;

    /**
     * 报警参数值
     */
    private String alarmParameterValue;

    /**
     * 报警参数编码
     */
    private String faultParameterCode;

    /**
     * 报警参数值
     */
    private String faultParameterValue;

    /**
     * 相机视角
     */
    private List<Integer> eye;

    /**
     * 人眼视角
     */
    private List<Integer> center;

    private List<TblItemParameterVO> parameterList;

//    /**
//     * 摄像设备
//     */
//    private ListVideoItemResultDTO videoInfo;
}
