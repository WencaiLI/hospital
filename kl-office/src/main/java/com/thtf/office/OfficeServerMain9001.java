package com.thtf.office;

import com.thtf.office.common.annotation.EnableIbsFeignClients;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.thtf.office", "com.thtf.office.common"})
@EnableDiscoveryClient
@MapperScan("com.thtf.office.mapper")
@EnableTransactionManagement
@EnableIbsFeignClients
public class OfficeServerMain9001 {
    public static void main(String[] args) {
        SpringApplication.run(OfficeServerMain9001.class, args);
    }
}
