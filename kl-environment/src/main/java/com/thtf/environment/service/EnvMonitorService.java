package com.thtf.environment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.common.dto.itemserver.ItemTotalAndOnlineAndAlarmNumDTO;
import com.thtf.environment.dto.PageInfoVO;
import com.thtf.environment.entity.TblHistoryMoment;
import com.thtf.environment.vo.*;

import java.util.List;

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
    PageInfoVO listItemInfo(EnvMonitorItemParamVO paramVO);

    /**
     * @Author: liwencai
     * @Description:
     * @Date: 2022/10/27
     * @Param: itemCode:
     * @Return: java.util.List<com.thtf.environment.vo.ItemParameterInfoVO>
     */
    List<ItemParameterInfoVO> listParameter(String itemCode);

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
    PageInfoVO listGroupedItemAlarmInfo(String sysCode,String groupName,String areaName,String keyword,Integer pageNumber,Integer pageSize);


    List<GroupAlarmInfoVO> getGroupAlarmDisplayInfo(String sysCode, String areaCode, String buildingCodes);
}
