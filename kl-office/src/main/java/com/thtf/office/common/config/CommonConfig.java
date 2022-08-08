//package com.thtf.office.common.config;
//
//import com.thtf.common.log.OperateLogAOP;
//import org.springframework.cloud.openfeign.EnableFeignClients;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @Auther: liwencai
// * @Date: 2022/8/4 12:23
// * @Description:
// */
//@Configuration
//@EnableFeignClients(basePackages = {"com.thtf.common"})
//public class CommonConfig {
//
//    /**
//     * @Author: liwencai
//     * @Description: 开启日志切面
//     * @Date: 2022/8/4
//     * @return: com.thtf.common.log.OperateLogAOP
//     */
//    @Bean
//    OperateLogAOP getOperateLogAOP(){
//        return new OperateLogAOP();
//    }
//}
