package com.thtf.face_recognition.entity.faceServer;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.thtf.face_recognition.vo.MegviiPersonInfo;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2023/1/7 09:58
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "tbl_megvii_alarm")
public class MegviiAlarmData{

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
    private Integer imageType;

    /**
     * 目标框在全景图中的位置：top：目标距离上方间距，
     * left：目标距离左方间距，
     * bottom：目标距离下方间距，
     * right：目标距离右方间距
     */
    private String targetRect;

    /**
     * 报警时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime alarmTime;

    /**
     * json MegviiPersonInfo 保存list
     */
    private String personInfo;

    private Integer alarmType;
}
