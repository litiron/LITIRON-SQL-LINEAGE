package com.litiron.code.lineage.sql.entity.table;

import com.litiron.code.lineage.sql.common.constants.TableConstants;
import com.litiron.code.lineage.sql.config.generator.Neo4jTableNodeGenerator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

/**
 * @description: 结点信息
 * @author: Litiron
 * @create: 2024-07-02 22:00
 **/
@Node(labels = "SqlLineageTableNodeEntity")
@Getter
@Setter
public class SqlLineageTableNodeEntity {
    /**
     * 唯一的id 由neo4j自动生成，应该也可以自定义
     */
    @Id
    @GeneratedValue(value = Neo4jTableNodeGenerator.class)
    private String id;

    private String tableName;

    private String databaseName = TableConstants.DEFAULT_DATABASE_NAME;

    private String connectionIp = TableConstants.DEFAULT_CONNECTION_IP;

    private Integer connectionPort = TableConstants.DEFAULT_CONNECTION_PORT;

    private String schemaName;

    private String tableComment;

    // 入边（上游关系）
    @Relationship(type = "SqlLineageTableEdgeEntity", direction = Relationship.Direction.INCOMING)
    private List<SqlLineageTableEdgeEntity> inRelationship;

    @Relationship(type = "SqlLineageTableEdgeEntity", direction = Relationship.Direction.OUTGOING)
    private List<SqlLineageTableEdgeEntity> outRelationShip;
}
