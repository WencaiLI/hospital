package com.thtf.elevator.service;

import com.thtf.common.dto.alarmserver.ItemAlarmNumberInfo;
import com.thtf.common.dto.itemserver.CodeAndNameDTO;
import com.thtf.common.dto.itemserver.PageInfoVO;
import com.thtf.common.entity.itemserver.TblItem;
import com.thtf.elevator.dto.DisplayInfoDTO;
import com.thtf.elevator.dto.ElevatorInfoResultDTO;
import com.thtf.elevator.dto.FloorInfoDTO;

import java.util.List;

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
    List<DisplayInfoDTO> displayInfo(String sysCode, String buildingCodes, String areaCode);

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
     * @Date: 2022/10/8
     * @Param: sysCode: 子系统编码
     * @Param: itemTypeCode: 设备类别编码
     * @Param: pageNum: 页号
     * @Param: pageSize: 页大小
     * @Return: com.thtf.elevator.vo.PageInfoVO
     */
    PageInfoVO getAllElevatorPage(String sysCode, String buildingCodes, String areaCode, String itemTypeCode, Integer onlineStatus, Integer pageNum, Integer pageSize);

    /**
     * @Author: liwencai
     * @Description: 所有故障设备信息
     * @Date: 2022/10/8
     * @Param: sysCode:
     * @Param: itemTypeCode:
     * @Param: pageNumber:
     * @Param: pageSize:
     * @Return: com.thtf.elevator.vo.PageInfoVO
     */
    PageInfoVO getAllAlarmPage(String sysCode, String buildingCodes, String areaCode, String itemTypeCode, Integer alarmCategory, Integer pageNumber, Integer pageSize);

    /**
     * @Author: liwencai
     * @Description: 报警统计
     * @Date: 2022/9/5
     * @Param sysCode:
     * @return: java.util.List<com.thtf.elevator.dto.KeyValueDTO>
     */
    List<ItemAlarmNumberInfo> getItemFaultStatistics(String sysCode,String buildingCodes,String areaCode, String startTime, String endTime);

    /**
     * @Author: liwencai
     * @Description: 获取楼层信息
     * @Date: 2022/10/8
     * @Return: java.util.List<com.thtf.elevator.dto.FloorInfoDTO>
     */
    List<FloorInfoDTO> getFloorInfo(String buildingCode,String sysCode);

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/12/1
     * @Param sysCode: 子系统编码
     * @return: java.util.List<com.thtf.common.dto.itemserver.CodeAndNameDTO>
     */
    List<CodeAndNameDTO> getItemType(String sysCode);
}
