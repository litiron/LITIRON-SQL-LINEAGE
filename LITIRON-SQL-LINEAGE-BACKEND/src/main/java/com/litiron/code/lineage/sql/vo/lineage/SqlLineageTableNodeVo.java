package com.litiron.code.lineage.sql.vo.lineage;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author 李日红
 * @description: 表维度的图数据库关系返回参数vo
 * @create 2025/2/22 17:18
 */
@Setter
@Getter
public class SqlLineageTableNodeVo {
    private String id;
    private String tableName;
    private String databaseName;
    private String connectionIp;
    private String schemaName;
    private String connectionPort;
    private String tableComment;
    private List<SqlLineageTableEdgeVo> outgoingRelationShip;
}
