package com.thtf.office.common.annotation;

/**
 * @Auther: liwencai
 * @Date: 2022/8/2 09:31
 * @Description:
 */
public enum RequestWay {

    INSERT("INSERT", "新增"),
    DELETE("DELETE", "删除"),
    UPDATE("SUPPLIER", "修改"),
    SELECT("SELECT","查询"),
    LOGIN("LOGIN","登录");

    private String code;
    private String desc;

    RequestWay(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }


    public String getDesc() {
        return desc;
    }

    /**
     * 通过code生成枚举
     *
     * @param code code
     * @return MyEnum or null
     */
    public static RequestWay valueByCode(String code) {
        if (code == null) {
            return null;
        }
        for (RequestWay e : values()) {
            if (code.equals(e.code)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 校验是否是该枚举
     *
     * @param code 枚举字符串
     * @return true or false
     */
    public static boolean isValidEnum(String code) {
        for (RequestWay enums : RequestWay.values()) {
            if (enums.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
