package com.thtf.office.common.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 初始化数据库执行器
 *
 * @author ligh
 */
@Component
@Slf4j
public class DatabaseInitializerInvoker implements InitializingBean {

    private final DataSource dataSource;

    @Value("${init.database.name:kl_hospital}")
    private String databaseName;

    @Value("${init.database.schema-script-path:sql/schema.sql}")
    private String sqlScriptPath;

    public DatabaseInitializerInvoker(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 填充属性后调用
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Connection connection = dataSource.getConnection();
        String sql = "SELECT COUNT(*) FROM information_schema.TABLES WHERE table_schema = ? GROUP BY table_schema";
        PreparedStatement prepareStatement = connection.prepareStatement(sql);
        prepareStatement.setString(1, databaseName);
        ResultSet resultSet = prepareStatement.executeQuery();
        if(!resultSet.next()) {
            log.info("初始化数据库开始");
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            Resource resource = new ClassPathResource(sqlScriptPath);
            populator.addScript(resource);
            DatabasePopulatorUtils.execute(populator, dataSource);
            log.info("成功初始化数据库");
        }
    }
}
