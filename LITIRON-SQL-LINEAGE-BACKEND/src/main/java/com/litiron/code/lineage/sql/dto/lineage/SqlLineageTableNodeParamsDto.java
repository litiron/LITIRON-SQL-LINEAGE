package com.litiron.code.lineage.sql.dto.lineage;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 李日红
 * @description: 表维度的图数据库关系查询参数dto
 * @create 2025/2/22 16:48
 */
@Setter
@Getter
public class SqlLineageTableNodeParamsDto {
    /*
     * 连接信息的id，根据此id获取IP和端口号
     */
    private String id;
    private String databaseName;
    /*
     * pgsql类型下才有schema
     */
    private String schemaName;
    private String tableName;
}
