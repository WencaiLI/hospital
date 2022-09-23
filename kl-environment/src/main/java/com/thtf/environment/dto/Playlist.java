package com.thtf.environment.dto;

import lombok.Data;

/**
 * @Author: liwencai
 * @Date: 2022/9/22 22:26
 * @Description: 播单
 */
@Data
public class Playlist {
    private Long playId; // 播放id
    private Long playTitle; // 播放主题
    private String playContent; // 播放信息
}
