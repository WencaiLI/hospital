package com.thtf.elevator.service;

import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.itemserver.CodeAndNameDTO;
import com.thtf.elevator.dto.DisplayInfoDTO;
import com.thtf.elevator.dto.ElevatorAlarmResultDTO;
import com.thtf.elevator.dto.ElevatorInfoResultDTO;
import com.thtf.elevator.dto.ItemFaultStatisticsDTO;
import com.thtf.elevator.vo.QueryItemParamVO;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/9/2 10:19
 * @Description:
 */
public interface ElevatorService {
    /**
     * @Author: liwencai
     * @Description: 前端展示数据
     * @Date: 2022/9/5
     * @Param sysCode: 子系统编码
     * @Param itemType: 设备类别
     * @return: java.util.List<com.thtf.elevator.dto.DisplayInfoDTO>
     */
    List<DisplayInfoDTO> displayInfo(String sysCode, String buildingCodes, String areaCode);

    /**
     * @Author: liwencai
     * @Description: 获取电梯的设备参数
     * @Date: 2022/9/5
     * @Param itemCodeList: 设备类别编码集
     * @return: java.util.List<com.thtf.elevator.dto.ElevatorInfoResultDTO>
     */
    List<ElevatorInfoResultDTO> listElevatorItemByCodeList(List<String> itemCodeList);

    /**
     * @Author: liwencai
     * @Description: 所有电梯设备信息
     * @Date: 2022/10/8
     * @Param: sysCode: 子系统编码
     * @Param: itemTypeCode: 设备类别编码
     * @Param: pageNum: 页号
     * @Param: pageSize: 页大小
     * @Return: com.thtf.elevator.vo.PageInfoVO
     */
    PageInfo<ElevatorInfoResultDTO> listElevatorItemPage(String sysCode, String buildingCodes, String areaCode, String itemTypeCode, Integer onlineStatus, Integer pageNum, Integer pageSize);

    /**
     * @Author: liwencai
     * @Description: 所有故障设备信息
     * @Date: 2022/10/8
     * @Param: sysCode: 子系统编码
     * @Param: itemTypeCode: 设备类别
     * @Param: pageNumber: 页号
     * @Param: pageSize: 页大小
     * @Return: com.thtf.elevator.vo.PageInfoVO
     */
    PageInfo<ElevatorAlarmResultDTO> listElevatorAlarmPage(String sysCode, String buildingCodes, String areaCode, String itemTypeCode, Integer alarmCategory, Integer pageNumber, Integer pageSize);

    /**
     * @Author: liwencai
     * @Description: 报警统计
     * @Date: 2022/9/5
     * @Param sysCode: 子系统编码
     * @return: java.util.List<com.thtf.elevator.dto.KeyValueDTO>
     */
    ItemFaultStatisticsDTO getItemFaultStatistics(String sysCode, String buildingCodes, String areaCode, String itemTypeCode, String startTime, String endTime);


    /**
     * @Author: liwencai
     * @Description: 获取设备类别（末级设备）
     * @Date: 2022/12/1
     * @Param sysCode: 子系统编码
     * @return: java.util.List<com.thtf.common.dto.itemserver.CodeAndNameDTO>
     */
    List<CodeAndNameDTO> listItemTypeLeaf(String sysCode);

    /**
     * @Author: liwencai
     * @Description: 查询设备信息
     * @Date: 2023/3/8
     * @Param queryItemParamVO:
     * @Return: java.lang.Object
     */
    List<ElevatorInfoResultDTO> queryItem(QueryItemParamVO queryItemParamVO);
}
