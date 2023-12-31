package com.thtf.environment.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/9/22 22:26
 * @Description: 播单
 */
@Data
public class Playlist {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long playId; // 播放id
    private String playTitle; // 播放主题
    private String playContent; // 播放信息
}
