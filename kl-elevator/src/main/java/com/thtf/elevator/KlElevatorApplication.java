package com.thtf.elevator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@SpringBootApplication
@EnableDiscoveryClient
//@MapperScan("com.thtf.office.mapper")
@EnableTransactionManagement
@EnableFeignClients("com.thtf.common")
@ComponentScan(basePackages = {"com.thtf.elevator","com.thtf.common"})
public class KlElevatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(KlElevatorApplication.class, args);
    }

}
