package com.thtf.office.controller;

import com.thtf.office.common.response.JsonResult;
import com.thtf.office.service.*;
import com.thtf.office.vo.VehicleStatisticsParamVO;
import com.thtf.office.vo.VehicleStatisticsResultVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

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
    @PostMapping("/vehicleStatus")
    public ResponseEntity<JsonResult<List<VehicleStatisticsResultVO>>> getVehicleStatus(@RequestBody VehicleStatisticsParamVO paramVO){
        return ResponseEntity.ok(JsonResult.success(vehicleStatisticsService.getVehicleStatus(paramVO)));
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
    public ResponseEntity<JsonResult<List>> rankingsOfOrg(@RequestBody VehicleStatisticsParamVO paramVO){
        return ResponseEntity.ok(JsonResult.error("失败"));
    }

    /**
     * @Author: liwencai
     * @Description: 部门用车频次排行榜
     * @Date: 2022/7/27
     * @Param paramVOp:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.util.List>>
     */
    @PostMapping("/rankingsOfVeh")
    public ResponseEntity<JsonResult<List>> rankingsOfVeh(@RequestBody VehicleStatisticsParamVO paramVO){
        return ResponseEntity.ok(JsonResult.error("失败"));
    }

    /**
     * @Author: liwencai
     * @Description: 司机出车频次排行榜
     * @Date: 2022/7/27
     * @Param null:
     * @return: null
     */
    @PostMapping("/rankingsOfDri")
    public ResponseEntity<JsonResult<List>> rankingsOfDri(@RequestBody VehicleStatisticsParamVO paramVO){
        return ResponseEntity.ok(JsonResult.error("失败"));
    }

    /**
     * @Author: liwencai
     * @Description: 车辆维保频次排行榜
     * @Date: 2022/7/27
     * @Param paramVOp:
     * @return: org.springframework.http.ResponseEntity<com.thtf.office.common.response.JsonResult<java.util.List>>
     */
    @PostMapping("/rankingsOfMai")
    public ResponseEntity<JsonResult<List>> rankingsOfMai(@RequestBody VehicleStatisticsParamVO paramVO){
        return ResponseEntity.ok(JsonResult.error("失败"));
    }
}
