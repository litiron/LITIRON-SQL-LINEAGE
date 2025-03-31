package com.litiron.code.lineage.sql.config.generator;

import com.litiron.code.lineage.sql.entity.column.SqlLineageColumnNodeEntity;
import com.litiron.code.lineage.sql.utils.TableNodeUtils;
import org.springframework.data.neo4j.core.schema.IdGenerator;

/**
 * @description: node节点唯一id生成
 * @author: Litiron
 * @create: 2025-03-08 23:46
 **/
public class Neo4jColumnNodeGenerator implements IdGenerator<String> {

    @Override
    public String generateId(String primaryLabel, Object entity) {
        SqlLineageColumnNodeEntity sqlLineageColumnNodeEntity = (SqlLineageColumnNodeEntity) entity;
        return TableNodeUtils.generateKey(sqlLineageColumnNodeEntity.getDatabaseName(), sqlLineageColumnNodeEntity.getSchemaName(),
                sqlLineageColumnNodeEntity.getTableName(), sqlLineageColumnNodeEntity.getColumnName());
    }
}
