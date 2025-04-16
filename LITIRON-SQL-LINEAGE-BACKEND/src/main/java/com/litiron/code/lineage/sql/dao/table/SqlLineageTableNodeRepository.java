package com.litiron.code.lineage.sql.dao.table;

import com.litiron.code.lineage.sql.entity.table.SqlLineageTableNodeEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

/**
 * @description: 结点dao层定义
 * @author: Litiron
 * @create: 2024-07-02 22:03
 **/
public interface SqlLineageTableNodeRepository extends Neo4jRepository<SqlLineageTableNodeEntity, String> {

    @Query("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r")
    void truncateDependency();


    @Query("MATCH (n) WHERE n.id = $id " +
            "OPTIONAL MATCH (n)-[outRel:SqlLineageTableEdgeEntity]->(outNode) " +      // 出边（下游）
            "OPTIONAL MATCH (inNode)-[inRel:SqlLineageTableEdgeEntity]->(n) " +        // 入边（上游）
            "RETURN n, " +
            "COLLECT(outRel) AS outgoingRelationships, " +
            "COLLECT(outNode) AS downstreamNodes, " +
            "COLLECT(inRel) AS incomingRelationships, " +
            "COLLECT(inNode) AS upstreamNodes")
    SqlLineageTableNodeEntity findNodeWithAllRelationships(String id);
}
