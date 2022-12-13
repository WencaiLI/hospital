package com.thtf.face_recognition.common.enums;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/12/7 00:17
 * @Description:
 */
@SuppressWarnings("All")
public enum  MegviiEventLevelEnum {
    LEVEL_ONE(1L,"高"),
    LEVEL_TWO(2L,"中"),
    LEVEL_THREE(3L,"低");
    private Long id;
    private String desc;

    MegviiEventLevelEnum(Long id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Long getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * @Author: liwencai
     * @Description: 根据事件id获取事件描述
     * @Date: 2022/12/6
     * @Param eventTypeId:
     * @Return: java.lang.String
     */
    public static String getMegviiEventLevelDescByTypeId(Long eventLevelId){
        for (MegviiEventLevelEnum megviiEventLevelEnum : MegviiEventLevelEnum.values()) {
            if(megviiEventLevelEnum.getId().equals(eventLevelId)){
                return megviiEventLevelEnum.getDesc();
            }
        }
        throw new IllegalArgumentException("name is invalid");
    }
}
