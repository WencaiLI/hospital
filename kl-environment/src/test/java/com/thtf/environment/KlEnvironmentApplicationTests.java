package com.thtf.environment;

import com.thtf.environment.config.ParameterConfigNacos;
import com.thtf.environment.service.EnvMonitorService;
import com.thtf.environment.service.impl.EnvMonitorServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KlEnvironmentApplicationTests {

    @Autowired
    ParameterConfigNacos parameterConfigNacos;

    @Autowired
    EnvMonitorServiceImpl envMonitorService;

//    @Test
//    void contextLoads() {
//        System.out.println(parameterInfoConfigInNacos.getItemParameterCodeList());
//    }

    @Test
    void x(){
        System.out.println(parameterConfigNacos.getItemTypeAndParameterTypeCodeList());
        envMonitorService.getParameterInfo();
    }

}
