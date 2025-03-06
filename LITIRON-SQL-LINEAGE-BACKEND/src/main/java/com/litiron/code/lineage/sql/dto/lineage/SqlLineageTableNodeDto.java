package com.litiron.code.lineage.sql.dto.lineage;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 李日红
 * @description: 表维度的图数据库结点返回参数dto
 * @create 2025/2/22 17:14
 */
@Setter
@Getter
public class SqlLineageTableNodeDto {
    private String id;
    private String tableName;
    private String databaseName;
    private String connectionIp;
    private String schemaName;
    private String connectionPort;
    private String tableComment;
    private List<SqlLineageTableEdgeDto> outgoingRelationShip;
}
