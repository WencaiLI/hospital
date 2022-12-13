package com.thtf.face_recognition.common.enums;

/**
 * @Author: liwencai
 * @Date: 2022/12/8 15:33
 * @Description:
 */
public enum MegviiPersonTypeEnum {
    // 1员工，2访客，3重点人员
    TYPE_1(1,"员工"),
    TYPE_2(2,"访客"),
    TYPE_3(3,"重点访客");
    private final Integer id;
    private final String desc;

    public Integer getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    MegviiPersonTypeEnum(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    /**
     * @Author: liwencai
     * @Description: 根据事件id获取事件描述
     * @Date: 2022/12/6
     * @Param eventTypeId:
     * @Return: java.lang.String
     */
    public static String getMegviiPersonTypeEnumById(Integer personTypeId){
        for (MegviiPersonTypeEnum megviiPersonTypeEnum : MegviiPersonTypeEnum.values()) {
            if(megviiPersonTypeEnum.getId().equals(personTypeId)){
                return megviiPersonTypeEnum.getDesc();
            }
        }
        throw new IllegalArgumentException("name is invalid");
    }

}
