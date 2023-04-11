package com.thtf.environment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.thtf.common.dto.alarmserver.ItemAlarmInfoDTO;
import com.thtf.common.dto.alarmserver.ItemAlarmInfoVO;
import com.thtf.common.dto.itemserver.GroupAlarmInfoVO;
import com.thtf.common.dto.itemserver.ItemTotalAndOnlineAndAlarmNumDTO;
import com.thtf.common.dto.itemserver.ListParameterMapDTO;
import com.thtf.common.response.JsonResult;
import com.thtf.environment.dto.EChartsMoreVO;
import com.thtf.environment.dto.EnvItemMonitorDTO;
import com.thtf.environment.dto.PageInfoVO;
import com.thtf.environment.entity.TblHistoryMoment;
import com.thtf.environment.vo.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: liwencai
 * @Date: 2022/10/25 14:27
 * @Description:
 */
public interface EnvMonitorService extends IService<TblHistoryMoment> {

    /**
     * @Author: liwencai
     * @Description: 获取前端展示数据
     * @Date: 2022/10/25
     * @Param: sysCode: 子系统编码
     * @Return: com.thtf.environment.vo.EnvMonitorDisplayVO
     */
    ItemTotalAndOnlineAndAlarmNumDTO getDisplayInfo(String sysCode,String areaCode,String buildingCodes);

    /**
     * @Author: liwencai
     * @Description: 未处理报警数据统计
     * @Date: 2022/10/25
     * @Param: startTime: 开始时间
     * @Param: endTime: 结束时间
     * @Param: buildingCodes: 建筑编码集
     * @Param: areaCode: 区域编码
     * @Return: com.thtf.environment.vo.EChartsVO
     */
    EChartsVO getAlarmUnhandledStatistics(String sysCode,String buildingCodes, String areaCode, Boolean isHandled,String startTime, String endTime);

    /**
     * @Author: liwencai
     * @Description: 获取设备类别编码和名称集
     * @Date: 2022/10/25
     * @Param: sysCode: 子系统编码
     * @Return: java.util.List<com.thtf.environment.vo.CodeNameVO>
     */
    List<CodeNameVO> getItemTypeList(String sysCode);

    /**
     * @Author: liwencai
     * @Description: 获取设备信息
     * @Date: 2022/10/27
     * @Param: paramVO: 
     * @Return: java.util.List<com.thtf.environment.vo.EnvMonitorItemResultVO>
     */
    PageInfo<EnvMonitorItemResultVO> listItemInfo(EnvMonitorItemParamVO paramVO);

    /**
     * @Author: liwencai
     * @Description: 获取每小时的历史数据
     * @Date: 2022/10/27
     * @Param: itemCode: 设备编码
     * @Param: parameterCode: 参数编码
     * @Param: date: yyyy-MM-dd 格式日期
     * @Return: com.thtf.environment.vo.EChartsVO
     */
    EChartsVO getHourlyHistoryMoment(String itemCode,String itemTypeCode, String parameterCode, String date);

    /**
     * @Author: liwencai
     * @Description: 获取每日历史数据
     * @Date: 2022/10/27
     * @Param: itemCode: 设备编码
     * @Param: itemTypeCode: 设备类别编码
     * @Param: parameterCode: 参数编码
     * @Param: date: yyyy-MM-dd 格式日期
     * @Return: com.thtf.environment.vo.EChartsVO
     */
    EChartsVO getDailyHistoryMoment(String itemCode, String itemTypeCode, String parameterCode, String date);

    /**
     * @Author: liwencai
     * @Description: 
     * @Date: 2022/10/27
     * @Param: itemCode: 设备编码
     * @Param: itemTypeCode: 设备类别编码
     * @Param: parameterCode: 参数编码
     * @Param: date: yyyy-MM-dd 格式日期
     * @Return: com.thtf.environment.vo.EChartsVO
     */
    EChartsVO getMonthlyHistoryMoment(String itemCode, String itemTypeCode, String parameterCode, String date);

    /**
     * @Author: liwencai
     * @Description: 获取分组信息
     * @Date: 2022/10/30
     * @Param sysCode: 子系统编码
     * @return: com.thtf.environment.dto.PageInfoVO
     */
    PageInfoVO listGroupedItemAlarmInfo(String sysCode,String buildingCodes, String areaCodes, String groupName,String areaName,String keyword,Integer pageNumber,Integer pageSize);


    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/23
     * @Param sysCode:
     * @Param areaCode:
     * @Param buildingCodes:
     * @return: java.util.List<com.thtf.environment.vo.GroupAlarmInfoVO>
     */
    List<GroupAlarmInfoVO> getGroupAlarmDisplayInfo(String sysCode, String areaCode, String buildingCodes);

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/11/23
     * @Param sysCode:
     * @Param itemTypeCode:
     * @return: java.util.List<com.thtf.environment.vo.ItemCodeAndNameAndTypeVO>
     */
    List<ItemCodeAndNameAndTypeVO> listItemCodeAndTypeCodeByTypeCode(String sysCode, String itemTypeCode);

    /**
     * @Author: liwencai
     * @Description: 监测点位信息
     * @Date: 2022/12/5
     * @Param itemCode: 设备编码
     * @Return: java.lang.Object
     */
    EnvItemMonitorDTO getMonitorPointInfo(String itemCode);

    Object listParameterMap(ListParameterMapDTO listParameterMapDTO);

    /**
     * @Author: liwencai
     * @Description: 24小时维度统计报警数量
     * @Date: 2023/2/1
     * @Param sysCode: 子系统编码
     * @Param buildingCodes: 建筑区域编码集合
     * @Param areaCode: 区域编码
     * @Param isHandled: 是否包含已处理报警
     * @Param startTime: 开始时间
     * @Param endTime: 结束时间
     * @Return: com.thtf.environment.dto.EChartsMoreVO
     */
    EChartsMoreVO getTotalAlarmHourly(String sysCode, String buildingCodes, String areaCode, Boolean isHandled, String startTime, String endTime);

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2023/2/28
     * @Param param:
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.common.dto.alarmserver.ItemAlarmInfoDTO>>
     */
    JsonResult<List<ItemAlarmInfoDTO>> getItemsAlarmInfo(ItemAlarmInfoVO param);

    /**
     * 获取监测参数单位
     * @author liwencai
     * @param sysCode 子系统编码
     * @param itemTypeCodeList 设备类别编码集
     * @return {@link JsonResult<Map<String,String>>}
     */
    List<CodeUnitVO> getParameterUnit(String sysCode, List<String> itemTypeCodeList);
}
