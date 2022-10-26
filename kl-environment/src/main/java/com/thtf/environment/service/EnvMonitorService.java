package com.thtf.environment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.common.dto.itemserver.ItemTotalAndOnlineAndAlarmNumDTO;
import com.thtf.environment.entity.TblHistoryMoment;
import com.thtf.environment.vo.CodeNameVO;
import com.thtf.environment.vo.EChartsVO;
import com.thtf.environment.vo.EnvMonitorDisplayVO;
import org.springframework.stereotype.Repository;

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
    EChartsVO getAlarmUnhandledStatistics(String buildingCodes, String areaCode, String startTime, String endTime);

    /**
     * @Author: liwencai
     * @Description: 获取设备类别编码和名称集
     * @Date: 2022/10/25
     * @Param: sysCode: 子系统编码
     * @Return: java.util.List<com.thtf.environment.vo.CodeNameVO>
     */
    List<CodeNameVO> getItemTypeList(String sysCode);
}
