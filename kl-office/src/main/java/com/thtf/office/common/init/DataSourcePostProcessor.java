package com.thtf.office.common.init;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.sql.DataSource;

/**
 * DataSource的beanPostProcess
 *
 * @author ligh
 */
@Configuration
public class DataSourcePostProcessor implements BeanPostProcessor, Ordered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Autowired
    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource && "druidDataSource".equals(beanName)) {
            // force initialization of this bean as soon as we see a DataSource
            this.beanFactory.getBean(com.thtf.office.common.init.DatabaseInitializerInvoker.class);
        }
        return bean;
    }

}
