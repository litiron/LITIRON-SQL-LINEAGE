package com.litiron.code.lineage.sql.entity;

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
@Node(labels = "表信息")
@Getter
@Setter
public class SqlLineageNodeEntity {
    /**
     * 唯一的id 由neo4j自动生成，应该也可以自定义
     */
    @Id
    @GeneratedValue
    private String id;
    private String tableName;
    private String databaseName;
    private String connectionIp;
    private String schemaName;
    private String connectionPort;
    private String tableComment;
    @Relationship(type = "joinRelationShip", direction = Relationship.Direction.OUTGOING)
    private List<SqlLineageEdgeEntity> outgoingRelationShip;
}
