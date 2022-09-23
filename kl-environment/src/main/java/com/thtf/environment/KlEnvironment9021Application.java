package com.thtf.environment;

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
@ComponentScan(basePackages = {"com.thtf.environment","com.thtf.common"})
public class KlEnvironment9021Application {

    public static void main(String[] args) {
        SpringApplication.run(KlEnvironment9021Application.class, args);
    }

}
