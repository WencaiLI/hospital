package com.thtf.face_recognition.domain.alarmMng.alarmDetail;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.thtf.face_recognition.domain.alarmMng.enums.ImageTypeEnum;
import com.thtf.face_recognition.domain.alarmMng.valueObject.FaceRect;
import com.thtf.face_recognition.domain.alarmMng.valueObject.PersonInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 人脸识别详细信息
 * @author liwencai
 * @since 2023/5/16
 */
@Getter
@Setter
public class AlarmDetailDO {
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
     * json MegviiPersonInfo 保存list
     */
    private PersonInfo personInfo;

    /**
     * 报警类别
     */
    private Integer alarmType;
}
