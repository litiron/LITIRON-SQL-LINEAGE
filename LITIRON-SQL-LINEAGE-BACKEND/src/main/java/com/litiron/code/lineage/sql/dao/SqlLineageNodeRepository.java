package com.litiron.code.lineage.sql.dao;

import com.litiron.code.lineage.sql.entity.SqlLineageNodeEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

/**
 * @description: 结点dao层定义
 * @author: Litiron
 * @create: 2024-07-02 22:03
 **/
@Repository
public interface SqlLineageNodeRepository extends Neo4jRepository<SqlLineageNodeEntity, String> {

    @Query("MATCH (n:表信息) WHERE n.id = $id " +
            "OPTIONAL MATCH (n)-[outRel:joinRelationShip]->(outNode) " +      // 出边（下游）
            "OPTIONAL MATCH (inNode)-[inRel:joinRelationShip]->(n) " +        // 入边（上游）
            "RETURN n, " +
            "COLLECT(outRel) AS outgoingRelationships, " +
            "COLLECT(outNode) AS downstreamNodes, " +
            "COLLECT(inRel) AS incomingRelationships, " +
            "COLLECT(inNode) AS upstreamNodes")
    SqlLineageNodeEntity findNodeWithAllRelationships(String id);
}
