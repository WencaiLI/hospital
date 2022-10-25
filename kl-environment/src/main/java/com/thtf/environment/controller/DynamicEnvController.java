package com.thtf.environment.controller;

import com.thtf.environment.service.impl.DynamicEnvServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: liwencai
 * @Date: 2022/10/8 18:02
 * @Description: 动环监控接口
 */
@RestController
@RequestMapping("/dynamic_env")
@Slf4j
public class DynamicEnvController {

    @Resource
    DynamicEnvServiceImpl dynamicEnvService;


}
