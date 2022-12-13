package com.thtf.face_recognition.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/12/8 09:28
 * @Description:
 */
@Data
public class MegviiUserInfoDTO {

    /**
     * UUID
     */
    private String uuid;

    /**
     * 用户名
     */
    private Long birthday;

    /**
     * 用户类型，1员工，2访客，3重点人员
     */
    private Integer type;

    /**
     * 性别，0知，1男，2女
     */
    private Integer sex;

    /**
     * 卡号，员工非必须，唯一
     */
    private String cardNum;

    /**
     * 员工编码，员工非必须，唯一
     */
    private String code;

    /**
     * 邮箱
     */
    private String email;

    /**T
     * 进入时间
     */
    private Long entryTime;

    /**
     * 备注
     */
    private String ext;

    /**
     * 人员所属分组列表
     */
    private List<MegviiPersonInfo> groupList;

    /**
     * 加密身份证号，使用AAES加密规则(模式: ECB，填充:
     * pkcs5padding)加密后的手机号，密钥为安全参数配置中的加密因子
     */
    private String identifyNum;

    /**
     * 用户识别照片的url
     */
    private String imageUri;

    /**
     * 用户名
     */
    private String name;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 访问开始时间 yyyy-MM-dd
     */
    private String cardBegin;

    /**
     * 访问终止时间 yyyy-MM-dd
     */
    private String cardEnd;

    /**
     * 职位，员工非必须
     */
    private String postion;

    /**
     * 拜访起始时间(时间戳,毫秒),访客必须
     */
    private String visitStartTime;

    /**
     * 拜访结束时间(时间戳,毫秒),访客.必须
     */
    private String visitEndTime;

    /**
     * 拜访原因，访客非必须
     */
    private String visitReason;

    /**
     * 受访人的UUID,访客必须
     */
    private String visitedUuid;

    /**
     * 受访人的姓名，访客必须
     */
    private String visitedName;

    /**
     * 访客类型,访客必须，1普通访客, 2 VIP
     */
    private String visitType;

    /**
     * 访客状态，访客必须，true有效,false无效
     */
    private String visitedStatus;

    /**
     * 唯一标识，唯一
     */
    private String uniqueIdentify;

    /**
     * 访客所属单位，访客非必须
     */
    private String visitFirm;

    /**
     * 组织UUID
     */
    private String orgUuid;

    /**
     * 组织名称
     */
    private String orgName;
}
