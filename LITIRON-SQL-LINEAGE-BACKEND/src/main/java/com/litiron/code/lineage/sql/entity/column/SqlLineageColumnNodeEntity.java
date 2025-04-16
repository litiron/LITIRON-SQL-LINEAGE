package com.litiron.code.lineage.sql.entity.column;

import com.alibaba.druid.DbType;
import com.litiron.code.lineage.sql.config.generator.Neo4jColumnNodeGenerator;
import com.litiron.code.lineage.sql.dto.lineage.column.SqlLineageColumnDependencyDto;
import com.litiron.code.lineage.sql.utils.TableNodeUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 字段节点
 * @author: Litiron
 * @create: 2025-03-16 23:23
 **/
@Getter
@Setter
@Node(labels = "SqlLineageColumnNodeEntity")
public class SqlLineageColumnNodeEntity {

    @Id
    @GeneratedValue(generatorClass = Neo4jColumnNodeGenerator.class)
    private String id;

    private String connectionIp;

    private Integer port;

    private String databaseName;

    private String schemaName;

    private String tableName;

    private String databaseType;

    private String columnName;

    @Relationship(type = "SqlLineageColumnEdgeEntity", direction = Relationship.Direction.OUTGOING)
    private List<SqlLineageColumnEdgeEntity> outRelationShip = new ArrayList<>();

    public String getId() {
        return TableNodeUtils.generateKey(databaseName, schemaName, tableName, columnName);
    }

    public static SqlLineageColumnNodeEntity convert(SqlLineageColumnDependencyDto columnDependencyDto) {
        SqlLineageColumnNodeEntity columnNodeEntity = new SqlLineageColumnNodeEntity();
        columnNodeEntity.setSchemaName(columnDependencyDto.getSourceSchema());
        columnNodeEntity.setTableName(columnDependencyDto.getSourceTable());
        columnNodeEntity.setDatabaseType(DbType.postgresql.toString());
        columnNodeEntity.setColumnName(columnDependencyDto.getTargetColumn());
        return columnNodeEntity;
    }


    public void convertEdge(List<SqlLineageColumnNodeEntity> outgoingNodeList) {
        for (SqlLineageColumnNodeEntity nodeEntity : outgoingNodeList) {
            SqlLineageColumnEdgeEntity edge = new SqlLineageColumnEdgeEntity();
            edge.setSqlLineageColumnNodeEntity(nodeEntity);
            edge.setBusinessId(TableNodeUtils.generateKey(nodeEntity.getId(), this.getId()));
            this.getOutRelationShip().add(edge);
        }
    }
}
