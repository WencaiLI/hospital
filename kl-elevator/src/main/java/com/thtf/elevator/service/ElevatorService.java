package com.thtf.elevator.service;

import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.alarmserver.ItemAlarmNumberInfo;
import com.thtf.common.dto.itemserver.ItemNestedParameterVO;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.elevator.dto.DisplayInfoDTO;
import com.thtf.elevator.dto.ElevatorAlarmResultDTO;
import com.thtf.elevator.dto.ElevatorInfoResultDTO;
import com.thtf.elevator.dto.KeyValueDTO;

import java.util.List;
import java.util.Map;

/**
 * @Auther: liwencai
 * @Date: 2022/9/2 10:19
 * @Description:
 */
public interface ElevatorService {
    /**
     * @Author: liwencai
     * @Description: 前端展示数据
     * @Date: 2022/9/5
     * @Param sysCode:
     * @Param itemType:
     * @return: java.util.List<com.thtf.elevator.dto.DisplayInfoDTO>
     */
    List<DisplayInfoDTO> displayInfo(String sysCode, String itemType);

    /**
     * @Author: liwencai
     * @Description: 报警数量
     * @Date: 2022/9/5
     * @Param sysCode:
     * @return: java.lang.Integer
     */
    Integer alarmNumber(String sysCode);

    /**
     * @Author: liwencai
     * @Description: 获取电梯的设备参数
     * @Date: 2022/9/5
     * @Param itemCodeList:
     * @return: java.util.List<com.thtf.elevator.dto.ElevatorInfoResultDTO>
     */
    List<ElevatorInfoResultDTO> itemCodeList(List<String> itemCodeList,Boolean isNeedAreaName);

    /**
     * @Author: liwencai
     * @Description: 关联设备信息
     * @Date: 2022/9/5
     * @Param relationType:
     * @Param itemCode:
     * @return: java.util.List<com.thtf.common.entity.itemserver.TblItem>
     */
    List<TblItem> getItemRelInfo(String relationType, String itemCode);

    /**
     * @Author: liwencai
     * @Description: 所有电梯设备信息
     * @Date: 2022/9/5
     * @Param sysCode:
     * @return: java.util.List<com.thtf.elevator.dto.ElevatorInfoResultDTO>
     */
    PageInfo<ItemNestedParameterVO> getAllElevatorPage(String sysCode, Integer pageNum, Integer pageSize);

    /**
     * @Author: liwencai
     * @Description: 所有故障设备信息
     * @Date: 2022/9/5
     * @Param sysCode:
     * @return: java.util.List<com.thtf.elevator.dto.ElevatorAlarmResultDTO>
     */
    Map<String, Object> getAllAlarmPage(String sysCode, Integer pageNumber, Integer pageSize);

    /**
     * @Author: liwencai
     * @Description: 报警统计
     * @Date: 2022/9/5
     * @Param sysCode:
     * @return: java.util.List<com.thtf.elevator.dto.KeyValueDTO>
     */
    List<ItemAlarmNumberInfo> getItemFaultStatistics(String sysCode, String startTime, String endTime);
}
