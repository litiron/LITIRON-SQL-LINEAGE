package com.litiron.code.lineage.sql.dao.table;

import com.litiron.code.lineage.sql.entity.table.SqlLineageTableEdgeEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

/**
 * @description: 节点连接dao层定义
 * @author: Litiron
 * @create: 2024-07-02 22:03
 **/
public interface SqlLineageTableEdgeRepository extends Neo4jRepository<SqlLineageTableEdgeEntity, String> {

    /**
     * 根据唯一id查询
     *
     * @param uniqueId 唯一id
     * @return SqlLineageEdgeEntity
     */
    @Query("MATCH ()-[r:joinRelationShip]->() WHERE r.uniqueId = $uniqueId RETURN count(*)")
    Integer countByUniqueId(String uniqueId);

    @Query("MATCH (s)-[r:SqlLineageTableEdgeEntity]->(t) WHERE t.id = $id RETURN r")
    List<SqlLineageTableEdgeEntity> findUpStream(String id);
}
