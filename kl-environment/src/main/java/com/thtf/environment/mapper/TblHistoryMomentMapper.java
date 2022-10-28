package com.thtf.environment.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thtf.environment.dto.TimeValueDTO;
import com.thtf.environment.entity.TblHistoryMoment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/25 17:49
 * @Description:
 */
@Component
public interface TblHistoryMomentMapper extends BaseMapper<TblHistoryMoment> {

    List<String> selectHistoryMomentTables(String s);

    List<String> selectExistentTableName(@Param("tableNames") List<String> result);

    List<TimeValueDTO> getHourlyHistoryMoment(@Param("parameterCode") String parameterCode, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<TimeValueDTO> getDailyHistoryMoment(@Param("parameterCode") String parameterCode, @Param("startTime") String startTime, @Param("endTime")String endTime);

    List<TimeValueDTO> getMonthlyHistoryMoment(@Param("parameterCode") String parameterCode, @Param("startTime") String startTime, @Param("endTime")String endTime);
}