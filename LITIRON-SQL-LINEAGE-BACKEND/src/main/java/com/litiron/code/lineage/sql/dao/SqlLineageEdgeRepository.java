package com.litiron.code.lineage.sql.dao;

import com.litiron.code.lineage.sql.entity.SqlLineageEdgeEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

/**
 * @description: 节点连接dao层定义
 * @author: Litiron
 * @create: 2024-07-02 22:03
 **/
@Repository
public interface SqlLineageEdgeRepository extends Neo4jRepository<SqlLineageEdgeEntity, String> {

    /**
     * 根据唯一id查询
     *
     * @param uniqueId 唯一id
     * @return SqlLineageEdgeEntity
     */
    @Query("MATCH ()-[r:joinRelationShip]->() WHERE r.uniqueId = $uniqueId RETURN count(*)")
    Integer countByUniqueId(String uniqueId);
}
