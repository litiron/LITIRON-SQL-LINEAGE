package com.litiron.code.lineage.sql.config.generator;

import com.litiron.code.lineage.sql.entity.table.SqlLineageTableNodeEntity;
import com.litiron.code.lineage.sql.utils.TableNodeUtils;
import org.springframework.data.neo4j.core.schema.IdGenerator;

/**
 * @description: node节点唯一id生成
 * @author: Litiron
 * @create: 2025-03-08 23:46
 **/
public class Neo4jTableNodeGenerator implements IdGenerator<String> {

    @Override
    public String generateId(String primaryLabel, Object entity) {
        SqlLineageTableNodeEntity sqlLineageTableNodeEntity = (SqlLineageTableNodeEntity) entity;
        return TableNodeUtils.generateKey(sqlLineageTableNodeEntity.getConnectionIp(), sqlLineageTableNodeEntity.getDatabaseName(),
                sqlLineageTableNodeEntity.getSchemaName(), sqlLineageTableNodeEntity.getTableName());
    }
}
