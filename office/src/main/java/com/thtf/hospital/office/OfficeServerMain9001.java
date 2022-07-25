package com.thtf.hospital.office;

import com.thtf.common.annotation.EnableIbsFeignClients;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.thtf.adminserver", "com.thtf.common"})
@EnableDiscoveryClient
@MapperScan("com.thtf.adminserver.mapper")
@EnableTransactionManagement
@EnableIbsFeignClients
public class OfficeServerMain9001 {
    public static void main(String[] args) {
        SpringApplication.run(OfficeServerMain9001.class, args);
    }
}
