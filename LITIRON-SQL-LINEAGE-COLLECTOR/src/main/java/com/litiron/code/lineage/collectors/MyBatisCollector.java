package com.litiron.code.lineage.collectors;

import com.litiron.code.lineage.dto.DatabaseConnectionInfoDto;
import com.litiron.code.lineage.dto.SqlExecuteInfoDto;
import com.litiron.code.lineage.manager.RocketMQManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Objects;

/**
 * @description: mysql 框架下的SQL语句收集器
 * @author: Litiron
 * @create: 2025-04-12 14:06
 **/
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
@Slf4j
public class MyBatisCollector implements Interceptor {

    private final Environment environment;

    private final RocketMQManager rocketMQManager;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(handler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());

        // 获取SQL语句
        String sql = handler.getBoundSql().getSql();

        // 获取数据库连接信息
        Connection connection = (Connection) invocation.getArgs()[0];
        DatabaseMetaData metaData = connection.getMetaData();
        DatabaseConnectionInfoDto databaseConnectionInfoDto = extractDbInfo(metaData.getURL());
        if (Objects.isNull(databaseConnectionInfoDto)) {
            log.error("Failed to extract database connection info");
            return invocation.proceed();
        }
        SqlExecuteInfoDto executeInfo = SqlExecuteInfoDto.builder()
                .sql(sql)
                .databaseType(metaData.getDatabaseProductName())
                .databaseName(connection.getCatalog())
                .databaseHost(databaseConnectionInfoDto.getHost())
                .databasePort(databaseConnectionInfoDto.getPort())
                .executeTime(System.currentTimeMillis())
                .applicationName(environment.getProperty("spring.application.name"))
                .build();

        // 发送到消息队列
        rocketMQManager.send(executeInfo);

        return invocation.proceed();
    }

    private DatabaseConnectionInfoDto extractDbInfo(String url) {
        // 从JDBC URL中提取主机地址
        // jdbc:mysql://localhost:3306/test
        try {
            String[] parts = url.split("://")[1].split(":");
            String host = parts[0];
            Integer port = Integer.parseInt(parts[1].split("/")[0]);
            return DatabaseConnectionInfoDto.builder().host(host).port(port).build();
        } catch (Exception e) {
            log.error("Failed to extract host from JDBC URL: {}", url, e);
            return null;
        }
    }

    @Autowired
    public MyBatisCollector(Environment environment, RocketMQManager rocketMQManager) {
        this.environment = environment;
        this.rocketMQManager = rocketMQManager;
    }
}
