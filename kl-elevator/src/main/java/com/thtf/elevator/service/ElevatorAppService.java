package com.thtf.elevator.service;

import com.thtf.elevator.dto.DisplayInfoDTO;
import com.thtf.elevator.vo.AppAlarmInfoVO;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2023/2/28 18:48
 * @Description:
 */
public interface ElevatorAppService {

    List<DisplayInfoDTO> displayInfo(String sysCode, String buildingCodes, String areaCode);

    AppAlarmInfoVO getAlarmInfo(String sysCode, String buildingCodes, String areaCode);
}
