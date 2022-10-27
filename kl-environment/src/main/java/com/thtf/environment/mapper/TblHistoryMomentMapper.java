package com.thtf.environment.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thtf.environment.dto.TimeValueDTO;
import com.thtf.environment.entity.TblHistoryMoment;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/10/25 17:49
 * @Description:
 */
@Component
public interface TblHistoryMomentMapper extends BaseMapper<TblHistoryMoment> {

    List<String> selectHistoryMomentTables(String s);

    List<String> selectExistentTableName(List<String> result);

    List<TimeValueDTO> getHourlyHistoryMoment(@Param("parameterCode") String parameterCode,@Param("date") String date);

    List<TimeValueDTO> getDailyHistoryMoment(String parameterCode, String date);

    List<TimeValueDTO> getMonthlyHistoryMoment(String parameterCode, String date);
}