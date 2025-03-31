package com.litiron.code.lineage.sql.dao.column;

import com.litiron.code.lineage.sql.entity.column.SqlLineageColumnEdgeEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

/**
 * @description: sql血缘列数据访问层
 * @author: Litiron
 * @create: 2025-03-30 16:58
 **/
public interface SqlLineageColumnEdgeRepository extends Neo4jRepository<SqlLineageColumnEdgeEntity, String> {

    @Query("MATCH (c:SqlLineageColumnEdgeEntity) WHERE c.businessId = $id RETURN c")
    SqlLineageColumnEdgeEntity retrieveEdgeById(String id);
}
