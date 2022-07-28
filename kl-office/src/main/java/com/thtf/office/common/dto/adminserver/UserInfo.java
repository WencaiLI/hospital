package com.thtf.office.common.dto.adminserver;/**
 * DATE:
 * CREATE BY: zhangw
 */

import lombok.Data;

import java.util.List;

/**
 *@ClassName: UserInfo
 *@Description: OAuth用户
 *@Author
 *@Date 2022/5/12
 *@Version 1.0
 *
 */
@Data
public class UserInfo {
    /**
     *生日
     */
    private String birthday;
    /**
     *性别
     */
    private Integer gender;
    /**
     *电话
     */
    private String mobile;
    /**
     *创建时间
     */
    private String createdate;
    /**
     *员工编号
     */
    private String employeeNumber;
    /**
     *真实姓名
     */
    private String realname;
    /**
     *部门
     */
    private String department;
    /**
     *邮箱
     */
    private String email;
    /**
     *用户名称
     */
    private String username;
    /**
     *
     */
    private String token;
    /**
     *用户id
     */
    private Long userId;
    /**
     *关联的建筑编码
     */
    private List<String> buildingCodes;

}
