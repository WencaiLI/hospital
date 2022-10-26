package com.thtf.environment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableFeignClients("com.thtf.common")
@MapperScan("com.thtf.environment.mapper")
@ComponentScan(basePackages = {"com.thtf.environment","com.thtf.common"})
public class KlEnvironment9021Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(KlEnvironment9021Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(KlEnvironment9021Application.class);
    }

}
