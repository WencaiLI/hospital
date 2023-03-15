package com.thtf.elevator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;

@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableFeignClients("com.thtf.common")
@ComponentScan(basePackages = {"com.thtf.elevator","com.thtf.common"})
@Slf4j
public class KlElevator9020Application {

    public static void main(String[] args) {
        try {
            SpringApplication.run(KlElevator9020Application.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
