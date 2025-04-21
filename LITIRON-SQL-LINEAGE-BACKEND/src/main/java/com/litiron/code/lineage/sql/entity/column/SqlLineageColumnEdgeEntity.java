package com.litiron.code.lineage.sql.entity.column;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * @description:
 * @author: Litiron
 * @create: 2025-03-27 23:13
 **/
@Getter
@Setter
@RelationshipProperties
public class SqlLineageColumnEdgeEntity {

    @RelationshipId
    private String id;

    // 业务逻辑唯一
    private String businessId;

    @TargetNode
    private SqlLineageColumnNodeEntity sqlLineageColumnNode;
}
