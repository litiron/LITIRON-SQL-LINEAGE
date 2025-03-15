package com.litiron.code.lineage.sql.config;

import com.litiron.code.lineage.sql.entity.SqlLineageNodeEntity;
import org.springframework.data.neo4j.core.schema.IdGenerator;

/**
 * @description: node节点唯一id生成
 * @author: Litiron
 * @create: 2025-03-08 23:46
 **/
public class Neo4jTableNodeGenerator implements IdGenerator<String> {

    private static final String NODE_ID_PATTERN = "%s:%s:%s:%s";

    @Override
    public String generateId(String primaryLabel, Object entity) {
        SqlLineageNodeEntity sqlLineageNodeEntity = (SqlLineageNodeEntity) entity;
        return String.format(NODE_ID_PATTERN, sqlLineageNodeEntity.getConnectionIp(), sqlLineageNodeEntity.getDatabaseName(), sqlLineageNodeEntity.getSchemaName(), sqlLineageNodeEntity.getTableName());
    }
}
