package com.litiron.code.lineage.sql.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litiron.code.lineage.sql.dto.database.*;

import java.util.List;
import java.util.Map;

/**
 * @description: 数据库相关操作类
 * @author: 李日红
 * @create: 2024/11/30 13:57
 */
public interface DatabaseComplexService {
    /**
     * @description: 检索所有数据库连接信息
     * @return: java.util.List<DatabaseConnectionDto>
     * @author: 李日红
     * @create: 2024/12/1 14:18
     */
    List<DatabaseConnectionDto> retrieveDatabaseConnectionInfo();

    /**
     * @param id       连接信息key
     * @param pgDbName pgSql选择的数据库信息
     * @return java.util.List<com.litiron.code.lineage.sql.dto.database.SchemaStructInfoDto>
     * @description: 目前是每次新增连接都会存放在thread local的map集合中，key相同的话就直接从map中获取，不再读取数据库
     * @author 李日红
     * @since 2024/12/2 17:43
     */
    List<DatabaseStructInfoDto> updateDatabaseConnection(String id, String pgDbName);

    /**
     * @description: 获取表内容信息
     * @param: queryTableDetailsParamsDto 查询表内容参数
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<java.util.Map < java.lang.String, java.lang.Object>>
     * @author: 李日红
     * @create: 2024/12/7 13:35
     */
    IPage<Map<String, Object>> retrieveTableDetailsByPage(TableDetailsParamsDto tableDetailsParamsDto);

    /**
     * @description: 根据连接信息获取pg的所有数据库信息
     * @param: id 连接信息id
     * @return: java.util.List<java.lang.String>
     * @author: 李日红
     * @create: 2025/2/21 11:37
     */
    List<String> retrievePgDatabasesInfo(String id);

    /**
     * @description: 获取指定用户拥有的数据库连接
     * @param: databaseConnectionParamsDto 查询参数
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<com.litiron.code.lineage.sql.dto.database.DatabaseConnectionDto>
     * @author: 李日红
     * @create: 2025/3/28 0:53
     */
    IPage<DatabaseConnectionDto> retrieveMyDatabaseConnectionInfo(QueryDatabaseConnectionParamsDto queryDatabaseConnectionParamsDto);

    /**
     * @description: 添加数据库连接
     * @param: databaseConnectionParamsDto 数据库连接信息dto
     * @return: void
     * @author: 李日红
     * @create: 2025/3/28 2:30
     */
    void addDatabaseConnectionInfo(DatabaseConnectionParamsDto databaseConnectionParamsDto);

    /**
     * @description: 删除数据库连接
     * @param: id 数据库连接id
     * @return: void
     * @author: 李日红
     * @create: 2025/3/28 2:38
     */
    void deleteDatabaseConnectionInfo(String id);

    /**
     * @description: 编辑数据库连接
     * @param: databaseConnectionParamsDto 数据库连接信息dto
     * @return: void
     * @author: 李日红
     * @create: 2025/3/28 2:45
     */
    void editDatabaseConnectionInfo(DatabaseConnectionParamsDto databaseConnectionParamsDto);
}
