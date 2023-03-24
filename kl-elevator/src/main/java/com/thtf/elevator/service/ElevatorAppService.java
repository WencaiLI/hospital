package com.thtf.elevator.service;

import com.github.pagehelper.PageInfo;
import com.thtf.elevator.dto.DisplayInfoDTO;
import com.thtf.elevator.vo.AppAlarmInfoVO;
import com.thtf.elevator.vo.AppItemSortDTO;
import com.thtf.elevator.vo.AppItemSortVO;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2023/2/28 18:48
 * @Description:
 */
public interface ElevatorAppService {

    /**
     * @Author: liwencai
     * @Description: 获取展示信息
     * @Date: 2023/3/15
     * @Param sysCode:
     * @Param buildingCodes:
     * @Param areaCode:
     * @Return: java.util.List<com.thtf.elevator.dto.DisplayInfoDTO>
     */
    List<DisplayInfoDTO> displayInfo(String sysCode, String buildingCodes, String areaCode);

    /**
     * @Author: liwencai
     * @Description: 获取报警信息
     * @Date: 2023/3/15
     * @Param sysCode:
     * @Param buildingCodes:
     * @Param areaCode:
     * @Return: com.thtf.elevator.vo.AppAlarmInfoVO
     */
    AppAlarmInfoVO getAlarmInfo(String sysCode, String buildingCodes, String areaCode);

    /**
     * @Author: liwencai
     * @Description: 获取设备信息
     * @Date: 2023/3/15
     * @Param param:
     * @Return: com.thtf.common.dto.itemserver.PageInfoVO<com.thtf.elevator.vo.AppItemSortVO>
     */
    PageInfo<AppItemSortVO> listItem(AppItemSortDTO param);
}
