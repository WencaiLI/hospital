package com.thtf.office.dto;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 司机调度信息
 * @author guola
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TblUserScheduleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    /**
     * 人员姓名
     */
    private String name;

    /**
     *
     * 当日出车次数
     */
    private int dayDriveNum;

    /**
     * 当月出车次数
     */
    private int monthDriveNum;


}
