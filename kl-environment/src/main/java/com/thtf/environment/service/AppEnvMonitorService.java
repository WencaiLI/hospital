package com.thtf.environment.service;

import com.thtf.environment.dto.AppEnvMonitorDisplayDTO;
import com.thtf.environment.dto.AppListAlarmParamDTO;
import com.thtf.environment.dto.KeyValueDTO;

import java.util.List;

/**
 * @Author: liwencai
 * @Date: 2022/12/15 21:10
 * @Description:
 */
public interface AppEnvMonitorService {
    /**
     * @Author: liwencai 
     * @Description:
     * @Date: 2022/12/15
     * @Param sysCode: 子系统编码
     * @Param areaCode: 区域编码
     * @Param buildingCodes: 建筑编码集
     * @Return: com.thtf.environment.dto.AppEnvMonitorDisplayDTO 
     */
    AppEnvMonitorDisplayDTO getDisplayInfo(String sysCode, String buildingCodes);

    /**
     * @Author: liwencai 
     * @Description:
     * @Date: 2022/12/15
     * @Param sysCode: 子系统编码
     * @Param buildingCodes: 建筑编码集
     * @Return: java.util.List<com.thtf.environment.dto.KeyValueDTO> 
     */
    List<KeyValueDTO> getAlarmCount(String sysCode, String buildingCodes);

    /**
     * @Author: liwencai 
     * @Description:
     * @Date: 2022/12/15
     * @Param paramDTO: 
     * @Return: java.lang.Object 
     */
    Object listAlarmUnhandled(AppListAlarmParamDTO paramDTO);

    
    /**
     * @Author: liwencai 
     * @Description:
     * @Date: 2022/12/15
     * @Param sysCode: 子系统编码
     * @Param buildingCodes: 建筑编码集
     * @Return: java.util.List<com.thtf.environment.dto.KeyValueDTO> 
     */
    List<KeyValueDTO> listGroupInfo(String sysCode, String buildingCodes);

    /**
     * @Author: liwencai 
     * @Description:
     * @Date: 2022/12/15
     * @Param sysCode: 子系统编码
     * @Return: java.util.List<com.thtf.environment.dto.KeyValueDTO> 
     */
    List<KeyValueDTO> listTypeInfo(String sysCode);
}
