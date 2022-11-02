package com.thtf.office.controller;

import com.thtf.common.log.OperateLog;
import com.thtf.common.log.OperateType;
import com.thtf.common.response.JsonResult;
import com.thtf.office.service.VehicleStatisticsService;
import com.thtf.office.vo.VehicleRankingsResultVO;
import com.thtf.office.vo.VehicleStatisticsParamVO;
import com.thtf.office.vo.VehicleStatisticsResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: liwencai
 * @Date: 2022/7/27 22:51
 * @Description: 公车相关信息数据统计
 */
@RestController
@RequestMapping("/vehicle/statistics")
@Slf4j
public class VehicleStatisticsController {

    @Resource
    VehicleStatisticsService vehicleStatisticsService;

    /**
     * @Author: liwencai
     * @Description: 车辆调度状态实时统计
     * @Date: 2022/7/27
     * @Param paramVOp:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.util.List>
     */
    @GetMapping("/vehicleStatus")
    @OperateLog(content = "车辆调度状态实时统计",operateType = OperateType.SELECT,operatePage = "车辆调度统计页面",systemCode = "kl-office",systemName = "办公微服务")
    public JsonResult<List<VehicleStatisticsResultVO>> getVehicleStatus(){
        return JsonResult.querySuccess(vehicleStatisticsService.getVehicleStatus(null));
    }

    /**
     * @Author: liwencai
     * @Description: 各类车辆出车统计
     * @Date: 2022/7/27
     * @Param paramVOp:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.util.List>
     */
    @PostMapping("/vehicleCategory")
    @OperateLog(content = "各类车辆出车统计",operateType = OperateType.SELECT,operatePage = "车辆调度统计页面",systemCode = "kl-office",systemName = "办公微服务")
    public JsonResult<List<VehicleStatisticsResultVO>> getVehicleCategory(@RequestBody VehicleStatisticsParamVO paramVO){
        return JsonResult.querySuccess(vehicleStatisticsService.getVehicleCategory(paramVO));
    }

    /**
     * @Author: liwencai
     * @Description: 车辆使用频次行榜
     * @Date: 2022/7/27
     * @Param paramVOp:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.util.List>
     */
    @PostMapping("/rankingsOfOrg")
    @OperateLog(content = "车辆使用频次行榜",operateType = OperateType.SELECT,operatePage = "车辆调度统计页面",systemCode = "kl-office",systemName = "办公微服务")
    public JsonResult<List<VehicleRankingsResultVO>> rankingsOfOrg(@RequestBody VehicleStatisticsParamVO paramVO){
        return JsonResult.querySuccess(vehicleStatisticsService.getRankings(getRankingsParam(paramVO.getStartTime(),paramVO.getEndTime(),"organization_name")));
    }

    /**
     * @Author: liwencai
     * @Description: 部门用车频次排行榜
     * @Date: 2022/7/27
     * @Param paramVOp:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.util.List>>
     */
    @PostMapping("/rankingsOfVeh")
    @OperateLog(content = "部门用车频次排行榜",operateType = OperateType.SELECT,operatePage = "车辆调度统计页面",systemCode = "kl-office",systemName = "办公微服务")
    public JsonResult<List<VehicleRankingsResultVO>> rankingsOfVeh(@RequestBody VehicleStatisticsParamVO paramVO){
        return JsonResult.querySuccess(vehicleStatisticsService.getRankings(getRankingsParam(paramVO.getStartTime(),paramVO.getEndTime(),"car_number")));
    }

    /**
     * @Author: liwencai
     * @Description: 司机出车频次排行榜
     * @Date: 2022/7/27
     * @Param: paramVO:
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.office.vo.VehicleRankingsResultVO>>
     */
    @PostMapping("/rankingsOfDri")
    @OperateLog(content = "司机出车频次排行榜",operateType = OperateType.SELECT,operatePage = "车辆调度统计页面",systemCode = "kl-office",systemName = "办公微服务")
    public JsonResult<List<VehicleRankingsResultVO>> rankingsOfDri(@RequestBody VehicleStatisticsParamVO paramVO){
        return JsonResult.querySuccess(vehicleStatisticsService.getRankings(getRankingsParam(paramVO.getStartTime(),paramVO.getEndTime(),"driver_name")));
    }

    /**
     * @Author: liwencai
     * @Description: 车辆维保频次排行榜
     * @Date: 2022/7/27
     * @Param paramVOp:
     * @return: org.springframework.http.com.thtf.common.response.JsonResult<java.util.List>>
     */
    @PostMapping("/rankingsOfMai")
    @OperateLog(content = "车辆维保频次排行榜",operateType = OperateType.SELECT,operatePage = "车辆调度统计页面",systemCode = "kl-office",systemName = "办公微服务")
    public JsonResult<List<VehicleRankingsResultVO>> rankingsOfMai(@RequestBody VehicleStatisticsParamVO paramVO){
        return JsonResult.querySuccess(vehicleStatisticsService.getMaintenanceRankings(paramVO));
    }

    /**
     * @Author: liwencai
     * @Description: 公车调度时长排行榜
     * @Date: 2022/7/28
     * @Param: paramVO:
     * @Return: com.thtf.common.response.JsonResult<java.util.List<com.thtf.office.vo.VehicleRankingsResultVO>>
     */
    @PostMapping("/rankingsOfSchWD")
    @OperateLog(content = "公车出车时长排行榜",operateType = OperateType.SELECT,operatePage = "车辆调度统计页面",systemCode = "kl-office",systemName = "办公微服务")
    public JsonResult<List<VehicleRankingsResultVO>> rankingsOfSchWD(@RequestBody VehicleStatisticsParamVO paramVO){
        try {
            return JsonResult.querySuccess(vehicleStatisticsService.rankingsOfSchWD(paramVO));
        }catch (Exception e){
            return JsonResult.error(e.getMessage());
        }
    }

    /**
     * @Author: liwencai
     * @Description: 排行榜统计Mybatis xml参数
     * @Date: 2022/7/28
     * @Param startTime: 排行统计时间
     * @Param endTime: 排行结束时间
     * @Param fieldName: 用于排行的字段（与数据库表中的字段名一致）
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String,Object> getRankingsParam(LocalDateTime startTime,LocalDateTime endTime,String fieldName){
        Map<String,Object> map = new HashMap<>();
        map.put("startTime",startTime);
        map.put("endTime",endTime);
        map.put("field",fieldName);
        return map;
    }
}
