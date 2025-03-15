package com.litiron.code.lineage.sql.config;

import com.litiron.code.lineage.sql.entity.SqlLineageEdgeEntity;
import com.litiron.code.lineage.sql.entity.SqlLineageNodeEntity;
import com.litiron.code.lineage.sql.utils.TableNodeUtils;
import org.springframework.data.neo4j.core.schema.IdGenerator;

/**
 * @description: relationShip节点唯一id生成
 * @author: Litiron
 * @create: 2025-03-08 23:46
 **/
public class Neo4jTableEdgeGenerator implements IdGenerator<String> {


    @Override
    public String generateId(String primaryLabel, Object entity) {
        SqlLineageEdgeEntity sqlLineageEdgeEntity = (SqlLineageEdgeEntity) entity;
        return TableNodeUtils.generateKey(sqlLineageEdgeEntity.getLeftTableName(), sqlLineageEdgeEntity.getRightTableName());
    }
}
