package com.thtf.office;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication(scanBasePackages = {"com.thtf.office", "com.thtf.office.common"})
@EnableDiscoveryClient
@MapperScan("com.thtf.office.mapper")
@EnableTransactionManagement
@EnableFeignClients
@ComponentScan(basePackages ={"com.thtf.common"})
public class OfficeServerMain9001 {
    public static void main(String[] args) {
        SpringApplication.run(OfficeServerMain9001.class, args);
    }
}
