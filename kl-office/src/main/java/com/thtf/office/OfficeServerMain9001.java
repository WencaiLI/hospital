package com.thtf.office;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.thtf.office.mapper")
@EnableTransactionManagement
@EnableFeignClients("com.thtf.common")
@ComponentScan(basePackages = {"com.thtf.office","com.thtf.common"})
//@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class OfficeServerMain9001 extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(OfficeServerMain9001.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(OfficeServerMain9001.class);
    }
}
