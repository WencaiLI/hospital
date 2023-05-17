package com.thtf.face_recognition.domain.alarmMng.enums;

/**
 * @author liwencai
 * @since 2023/5/16
 */
public enum ImageTypeEnum {
    SNAPSHOTS(0,"抓拍图"),
    PANORAMA(1,"全景图");

    private Integer id;

    private String desc;

    ImageTypeEnum(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public static ImageTypeEnum getEnumById(Integer id){
        for (ImageTypeEnum typeEnum : ImageTypeEnum.values()) {
            if(typeEnum.getId().equals(id)){
                return typeEnum;
            }
        }
        return null;
    }
}
