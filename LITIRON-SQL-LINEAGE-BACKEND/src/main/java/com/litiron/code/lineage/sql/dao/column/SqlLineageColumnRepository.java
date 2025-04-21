package com.litiron.code.lineage.sql.dao.column;

import com.litiron.code.lineage.sql.entity.column.SqlLineageColumnNodeEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

/**
 * @description: sql血缘列数据访问层
 * @author: Litiron
 * @create: 2025-03-30 16:58
 **/
public interface SqlLineageColumnRepository extends Neo4jRepository<SqlLineageColumnNodeEntity, String> {

    @Query("MATCH (c:SqlLineageColumnNodeEntity) WHERE c.id = $id RETURN c")
    SqlLineageColumnNodeEntity retrieveColumnNodeById(String id);

    @Query("MATCH (n:SqlLineageColumnNodeEntity) WHERE n.id = $id " +
            "OPTIONAL MATCH (n)-[outRel:SqlLineageColumnEdgeEntity]->(outNode) " +      // 出边（下游）
            "OPTIONAL MATCH (inNode)-[inRel:SqlLineageColumnEdgeEntity]->(n) " +        // 入边（上游）
            "RETURN n, " +
            "COLLECT(outRel) AS outgoingRelationships, " +
            "COLLECT(outNode) AS downstreamNodes, " +
            "COLLECT(inRel) AS incomingRelationships, " +
            "COLLECT(inNode) AS upstreamNodes")
    SqlLineageColumnNodeEntity findNodeWithAllRelationships(String id);
}
