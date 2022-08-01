package com.thtf.office.controller;

import com.thtf.office.common.response.JsonResult;
import com.thtf.office.service.*;
import com.thtf.office.vo.VehicleRankingsResultVO;
import com.thtf.office.vo.VehicleStatisticsParamVO;
import com.thtf.office.vo.VehicleStatisticsResultVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liwencai
 * @Date: 2022/7/27 22:51
 * @Description: 公车相关信息数据统计
 */
@RestController
@RequestMapping("/vehicle/statistics")
public class VehicleStatisticsController {
    @Resource
    VehicleStatisticsService vehicleStatisticsService;

    /**
     * @Author: liwencai
     * @Description: 车辆调度状态实时统计
     * @Date: 2022/7/27
     * @Param paramVOp:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.util.List>>
     */
    @GetMapping("/vehicleStatus")
    public ResponseEntity<JsonResult<List<VehicleStatisticsResultVO>>> getVehicleStatus(){
        return ResponseEntity.ok(JsonResult.success(vehicleStatisticsService.getVehicleStatus()));
    }

    /**
     * @Author: liwencai
     * @Description: 各类车辆出车统计
     * @Date: 2022/7/27
     * @Param paramVOp:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.util.List>>
     */
    @PostMapping("/vehicleCategory")
    public ResponseEntity<JsonResult<List<VehicleStatisticsResultVO>>> getVehicleCategory(@RequestBody VehicleStatisticsParamVO paramVO){
        List<VehicleStatisticsResultVO> result = vehicleStatisticsService.getVehicleCategory(paramVO);
        return ResponseEntity.ok(JsonResult.success(result));
    }

    /**
     * @Author: liwencai
     * @Description: 车辆使用频次行榜
     * @Date: 2022/7/27
     * @Param paramVOp:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.util.List>>
     */
    @PostMapping("/rankingsOfOrg")
    public ResponseEntity<JsonResult<List<VehicleRankingsResultVO>>> rankingsOfOrg(@RequestBody VehicleStatisticsParamVO paramVO){
        return ResponseEntity.ok(JsonResult.success(vehicleStatisticsService.getRankings(getRankingsParam(paramVO.getStartTime(),paramVO.getEndTime(),"organization_name"))));
    }

    /**
     * @Author: liwencai
     * @Description: 部门用车频次排行榜
     * @Date: 2022/7/27
     * @Param paramVOp:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.util.List>>
     */
    @PostMapping("/rankingsOfVeh")
    public ResponseEntity<JsonResult<List<VehicleRankingsResultVO>>> rankingsOfVeh(@RequestBody VehicleStatisticsParamVO paramVO){
        return ResponseEntity.ok(JsonResult.success(vehicleStatisticsService.getRankings(getRankingsParam(paramVO.getStartTime(),paramVO.getEndTime(),"car_number"))));
    }

    /**
     * @Author: liwencai
     * @Description: 司机出车频次排行榜
     * @Date: 2022/7/27
     * @Param null:
     * @return: null
     */
    @PostMapping("/rankingsOfDri")
    public ResponseEntity<JsonResult<List<VehicleRankingsResultVO>>> rankingsOfDri(@RequestBody VehicleStatisticsParamVO paramVO){
        return ResponseEntity.ok(JsonResult.success(vehicleStatisticsService.getRankings(getRankingsParam(paramVO.getStartTime(),paramVO.getEndTime(),"driver_name"))));
    }

    /**
     * @Author: liwencai
     * @Description: 车辆维保频次排行榜
     * @Date: 2022/7/27
     * @Param paramVOp:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.util.List>>
     */
    @PostMapping("/rankingsOfMai")
    public ResponseEntity<JsonResult<List<VehicleRankingsResultVO>>> rankingsOfMai(@RequestBody VehicleStatisticsParamVO paramVO){
        List<VehicleRankingsResultVO> result = vehicleStatisticsService.getMaintenanceRankings(paramVO);
        return ResponseEntity.ok(JsonResult.success(result));
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
