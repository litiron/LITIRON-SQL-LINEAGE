package com.litiron.code.lineage.sql.service.database;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litiron.code.lineage.sql.dto.database.DatabaseConnectionDto;
import com.litiron.code.lineage.sql.dto.database.QueryDatabaseConnectionParamsDto;
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
    List<DatabaseConnectionEntity> getAllDatabaseConnectionInfo();

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

    /**
     * @description: 查询某用户下的连接信息
     * @param: databaseConnectionParamsDto 查询参数
     * @return: java.util.List<com.litiron.code.lineage.sql.entity.database.DatabaseConnectionEntity>
     * @author: 李日红
     * @create: 2025/3/28 0:31
     */
    IPage<DatabaseConnectionDto> getDatabaseConnectionPageByUid(QueryDatabaseConnectionParamsDto queryDatabaseConnectionParamsDto);

    /**
     * @description: 添加数据库连接
     * @param: databaseConnectionEntity  数据库连接信息
     * @return: void
     * @author: 李日红
     * @create: 2025/3/28 2:32
     */
    void addDatabaseConnection(DatabaseConnectionEntity databaseConnectionEntity);

    /**
     * @description: 删除数据库连接
     * @param: id 数据库连接id
     * @return: void
     * @author: 李日红
     * @create: 2025/3/28 2:39
     */
    void deleteDatabaseConnection(String id);

    /**
     * @description: 编辑数据库连接信息
     * @param: databaseConnectionEntity 数据库连接信息
     * @return: void
     * @author: 李日红
     * @create: 2025/3/28 2:46
     */
    void editDatabaseConnection(DatabaseConnectionEntity databaseConnectionEntity);
}
