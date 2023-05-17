package com.thtf.face_recognition.domain.alarmMng.alarmDetail;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.thtf.face_recognition.domain.alarmMng.enums.ImageTypeEnum;
import com.thtf.face_recognition.domain.alarmMng.validate.AlarmDetailValidate;
import com.thtf.face_recognition.domain.alarmMng.valueObject.FaceRect;
import com.thtf.face_recognition.domain.alarmMng.valueObject.PersonInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author liwencai
 * @since 2023/5/16
 */
public class AlarmDetailBuilder {

    private AlarmDetailValidate alarmDetailValidate;

    public AlarmDetailBuilder(AlarmDetailValidate alarmDetailValidate) {
        this.alarmDetailValidate = alarmDetailValidate;
    }

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 设备类别
     */
    private String itemCode;
    /**
     * 图片url
     */
    private String imageUrl;

    /**
     * 图片url类型 0抓拍图 1全景图
     */
    private ImageTypeEnum imageType;

    /**
     * 目标框在全景图中的位置：top：目标距离上方间距，
     * left：目标距离左方间距，
     * bottom：目标距离下方间距，
     * right：目标距离右方间距
     */
    private FaceRect targetRect;

    /**
     * 报警时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime alarmTime;

    /**
     * 个人信息
     */
    private PersonInfo personInfo;

    /**
     * 报警类别
     */
    private Integer alarmType;


    void validate(){
        itemValidate();
    }

    void itemValidate(){
        alarmDetailValidate.shouldNotEmptyByItemCode(itemCode);
        // alarmDetailValidate.shouldExistByItemCode(itemCode);
    }

    public AlarmDetailBuilder id(Long id){
        this.id = id;
        return this;
    }

    public AlarmDetailBuilder itemCode(String itemCode){
        this.itemCode = itemCode;
        return this;
    }

    public AlarmDetailBuilder imageUrl(String imageUrl){
        this.imageUrl = imageUrl;
        return this;
    }


    public AlarmDetailBuilder imageType(Integer imageType){
        this.imageType = ImageTypeEnum.getEnumById(imageType);
        return this;
    }

    public AlarmDetailBuilder imageType(ImageTypeEnum imageTypeEnum){
        this.imageType = imageTypeEnum;
        return this;
    }


    public AlarmDetailBuilder targetRect(String targetRect){
        System.out.println(targetRect);
        List<FaceRect> targetRect1 = FaceRect.convertToFaceRectList(targetRect);
        if(CollectionUtils.isNotEmpty(targetRect1)){
            this.targetRect = targetRect1.get(0);
        }
        return this;
    }


    public AlarmDetailBuilder alarmTime(LocalDateTime alarmTime){
        this.alarmTime = alarmTime;
        return this;
    }


    public AlarmDetailBuilder personInfo(String personInfo){
        this.personInfo = PersonInfo.convertToPersonInfo(personInfo);
        return this;
    }

    public AlarmDetailBuilder personInfo(PersonInfo personInfo){
        this.personInfo = personInfo;
        return this;
    }


    public AlarmDetailBuilder alarmType(Integer alarmType){
        this.alarmType = alarmType;
        return this;
    }

    public AlarmDetailDO build(){
        validate();
        AlarmDetailDO alarmDetailDO = new AlarmDetailDO();
        alarmDetailDO.setId(id);
        alarmDetailDO.setItemCode(itemCode);
        alarmDetailDO.setImageUrl(imageUrl);
        alarmDetailDO.setImageType(imageType);
        alarmDetailDO.setAlarmTime(alarmTime);
        alarmDetailDO.setAlarmType(alarmType);
        alarmDetailDO.setPersonInfo(personInfo);
        alarmDetailDO.setTargetRect(targetRect);
        return alarmDetailDO;
    }
}
