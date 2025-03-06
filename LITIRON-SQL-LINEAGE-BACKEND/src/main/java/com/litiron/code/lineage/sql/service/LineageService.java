package com.litiron.code.lineage.sql.service;

import com.litiron.code.lineage.sql.dto.database.DatabaseConnectionDto;
import com.litiron.code.lineage.sql.dto.database.DatabaseStructInfoDto;
import com.litiron.code.lineage.sql.dto.lineage.SqlLineageTableNodeDto;
import com.litiron.code.lineage.sql.dto.lineage.SqlLineageTableNodeParamsDto;

import java.util.List;

/**
 * @description: 血缘关系Service
 * @author: 李日红
 * @create: 2025/2/8 17:46
 */
public interface LineageService {
    /**
     * @description: 获取数据库类型
     * @return: java.util.List<java.lang.String>
     * @author: 李日红
     * @create: 2025/2/8 20:39
     */
    List<String> retrieveDatabaseType();

    /**
     * @description: 获取所有连接信息
     * @return: java.util.List<com.litiron.code.lineage.sql.dto.database.DatabaseConnectionDto>
     * @author: 李日红
     * @create: 2025/2/9 20:25
     */
    List<DatabaseConnectionDto> retrieveAllConnection();

    /**
     * @description: 切换数据库连接
     * @param: id 连接id
     * @param: pgDbName pgSql选择的数据库信息
     * @return: java.util.List<com.litiron.code.lineage.sql.dto.database.SchemaStructInfoDto>
     * @author: 李日红
     * @create: 2025/2/20 14:15
     */
    List<DatabaseStructInfoDto> updateDatabaseConnection(String id, String pgDbName);

    /**
     * @description: 根据参数查询表维度的图数据库
     * @param: neo4jTableRetrieveParamsDto  查询dto
     * @return: java.util.List<com.litiron.code.lineage.sql.dto.lineage.Neo4jTableRetrieveDto>
     * @author: 李日红
     * @create: 2025/2/22 17:22
     */
    List<SqlLineageTableNodeDto> retrieveNeo4jTableInfo(SqlLineageTableNodeParamsDto sqlLineageTableNodeParamsDto);
}
