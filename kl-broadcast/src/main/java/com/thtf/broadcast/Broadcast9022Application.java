package com.thtf.broadcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableFeignClients("com.thtf.common")
@ComponentScan(basePackages = {"com.thtf.broadcast","com.thtf.common"})
public class Broadcast9022Application {
    public static void main(String[] args) {
        SpringApplication.run(Broadcast9022Application.class, args);
    }
}
