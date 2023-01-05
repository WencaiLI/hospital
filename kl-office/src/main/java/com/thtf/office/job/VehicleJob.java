package com.thtf.office.job;

import com.thtf.office.service.TblVehicleInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: liwencai
 * @Date: 2023/1/5 19:24
 * @Description:
 */
@Component
public class VehicleJob {
    @Autowired
    TblVehicleInfoService vehicleInfoService;
    @Scheduled(cron = "0/10 * * * * ?")  // 十秒执行一次
    public void refreshVehicleStatus(){
        System.out.println("执行");
        vehicleInfoService.updateInfoStatus();
    }
}
