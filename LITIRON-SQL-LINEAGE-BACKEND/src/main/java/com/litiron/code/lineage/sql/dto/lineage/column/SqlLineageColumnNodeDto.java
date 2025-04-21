package com.litiron.code.lineage.sql.dto.lineage.column;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 李日红
 * @description: 字段维度的图数据库结点返回参数dto
 * @create 2025/4/17 20:51
 */
@Setter
@Getter
public class SqlLineageColumnNodeDto {

    private String id;

    private List<SqlLineageColumnEdgeDto> outgoingRelationShip;

    private String databaseName;

    private String schemaName;

    private String tableName;

    private String tableComment;

    private String columnName;
    
    private String columnComment;
}
