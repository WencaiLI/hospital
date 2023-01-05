package com.thtf.face_recognition.dto;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/12/29 10:24
 * @Description:
 */
@Data
public class MegviiDeviceDTO {

    /**
     * 设备uuid
     */
    private String uuid;

    /**
     * 设备位置
     */
    private String location;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 设备类型 1 智㜭摄像头 2 智能面板 3 智能服务器 4 网路摄像机
     * 5 存储ᴽ务器 6、䰘禁控制器
     */
    private String deviceType;

    /**
     * 设备状态 3 在线 4离线
     */
    private Integer status;

    /**
     * 设备IP
     */
    private String ipAddress;

    /**
     * mac地址
     */
    private String mac;

    /**
     * 设备模式（详细模式请查看数据字段）
     */
    private Integer deviceMode;

    /**
     * 升级状态 0升级中 1升级成功 2 升级失败 4可升级 5升级完成
     */
    private Integer upgradeStatus;

    /**
     * 升级操作 0-更新版本（置灰）1-更行版本 2-历史版本
     */
    private Integer upgradeAction;

    /**
     * 当前版本
     */
    private String currentVersion;

    /**
     * 设备型号是否是固件
     */
    private Integer checkFirm;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 注册id
     */
    private String registerId;

    /**
     * 通道号
     */
    private String channelNum;
}
