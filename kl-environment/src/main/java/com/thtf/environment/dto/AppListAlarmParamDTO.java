package com.thtf.environment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/12/15 21:45
 * @Description:
 */
@Data
public class AppListAlarmParamDTO {


    /**
     * 子系统编码
     */
    private String sysCode;


    /**
     * 建筑编码
     */
    private String buildingCodes;

    /**
     * 楼层编码
     */
    private String areaCodes;

    /**
     * 区域id，监测区域
     */
    private List<String> groupIds;
    /**
     * 设备类别编码集
     */
    private List<String> itemTypeCodeList;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 页号
     */
    private Integer pageNumber;

    /**
     * 页大小
     */
    private Integer pageSize;

}
