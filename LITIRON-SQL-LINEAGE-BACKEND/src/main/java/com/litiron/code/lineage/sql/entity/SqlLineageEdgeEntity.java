package com.litiron.code.lineage.sql.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * @description: 结点连接线关系
 * @author: Litiron
 * @create: 2024-07-02 22:01
 **/
//@Node(labels = "表连接关系")
@RelationshipProperties
@Setter
@Getter
public class SqlLineageEdgeEntity {

    @RelationshipId
    private String id;
    /**
     * 目标结点
     */
    @TargetNode
    private SqlLineageNodeEntity to;

    private String relation;
}
