package com.thtf.environment.dto.convert;

import com.thtf.common.entity.alarmserver.TblAlarmRecordUnhandle;
import com.thtf.environment.dto.AlarmInfoOfLargeScreenDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/23 13:15
 * @Description:
 */
@Mapper(componentModel = "spring")
public interface AlarmConvert {

    @Mappings({
            @Mapping(target = "alarmId",source = "id"),
            @Mapping(target = "areaCode",source = "buildingAreaCode"),
            @Mapping(target = "areaName",source = "buildingAreaName")
    })
    AlarmInfoOfLargeScreenDTO toAlarmInfoOfLargeScreenDTO(TblAlarmRecordUnhandle alarmRecordUnhandle);

    List<AlarmInfoOfLargeScreenDTO> toAlarmInfoOfLargeScreenDTOList(List<TblAlarmRecordUnhandle> alarmRecordUnhandleList);
}
