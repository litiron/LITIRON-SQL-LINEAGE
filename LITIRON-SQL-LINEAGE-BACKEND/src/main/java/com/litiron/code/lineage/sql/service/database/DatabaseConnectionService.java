package com.litiron.code.lineage.sql.service.database;

import com.litiron.code.lineage.sql.entity.database.DatabaseConnectionEntity;

import java.util.List;

/**
 * @description: 数据库连接Service
 * @author: 李日红
 * @create: 2024/12/1 18:05
 */

public interface DatabaseConnectionService {
    /**
     * @description: 检索所有数据库连接信息
     * @return: java.util.List<DatabaseConnectionDto>
     * @author: 李日红
     * @create: 2024/12/1 14:18
     */
    List<DatabaseConnectionEntity> getaAllDatabaseConnectionInfo();

    /**
     * @description: 根据id获取连接信息
     * @param: id
     * @return: com.litiron.code.lineage.sql.dto.database.DatabaseConnectionDto
     * @author: 李日红
     * @create: 2024/12/1 18:04
     */
    DatabaseConnectionEntity getDatabaseConnectionInfoById(String id);

    /**
     * @description: 根据数据库类型获取数据库连接信息
     * @param: type  数据库类型
     * @return: java.util.List<com.litiron.code.lineage.sql.entity.database.DatabaseConnectionEntity>
     * @author: 李日红
     * @create: 2025/2/8 17:36
     */
    List<DatabaseConnectionEntity> getDatabaseConnectionInfoByType(String type);

    /**
     * @description: 获取所有数据库类型
     * @return: java.util.List<java.lang.String>
     * @author: 李日红
     * @create: 2025/2/8 20:40
     */
    List<String> getAllDatabaseType();
}
