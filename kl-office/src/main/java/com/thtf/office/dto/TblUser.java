package com.thtf.office.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author ligh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TblUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.ID_WORKER)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 人员编码
     */
    private String code;

    /**
     * 登录账号
     */
    private String loginName;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 人员姓名
     */
    private String name;

    /**
     * 性别 (男/女)
     */
    private String sex;

    /**
     * 入职日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date entryDate;

    /**
     * 组织机构编码
     */
    private String organizationCode;

    /**
     * 职员类别1:全职员工 2:兼职员工 3:实习员工
     */
    private Integer staffType;

    /**
     * 职位名称
     */
    private String positionTitle;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 身份证号
     */
    private String idCardNum;


    /**
     * 首页定义编码
     */
    private Long definedPageId;

    /**
     * 极光推送id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private String jpushRegistrationId;

    /**
     * 上次登录时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date loginUpdateDate;

    /**
     * 手机操作系统
     */
    private String os;

    /**
     * 是否推送报警通知（0：不推送；1：推送）
     */
    private Integer newAlarmNotification;

    /**
     * 是否推送新增工单通知（0：不推送；1：推送）
     */
    private Integer newOrderNotification;

    /**
     * 是否推送超时工单通知（0：不推送；1：推送）
     */
    private Integer timeoutOrderNotification;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 修改时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    /**
     * 删除人
     */
    private LocalDateTime deleteBy;
}
