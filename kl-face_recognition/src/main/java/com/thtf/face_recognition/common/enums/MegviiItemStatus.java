package com.thtf.face_recognition.common.enums;

/**
 * @Author: liwencai
 * @Date: 2023/1/4 23:45
 * @Description: 旷世设备状态枚举
 */
@SuppressWarnings("All")
public enum  MegviiItemStatus {

    ONLINE(3,"在线"),
    OFFLINE(4,"离线");

    private Integer id;
    private String desc;

    MegviiItemStatus(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public static String getMegviiEventLevelDescByTypeId(Integer itemStatus){
        for (MegviiItemStatus megviiItemStatus : MegviiItemStatus.values()) {
            if(megviiItemStatus.getId().equals(itemStatus)){
                return megviiItemStatus.getDesc();
            }
        }
        throw new IllegalArgumentException("name is invalid");
    }
}
