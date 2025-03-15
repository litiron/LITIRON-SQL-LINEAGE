package com.litiron.code.lineage.sql.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.litiron.code.lineage.sql.common.BusinessException;
import com.litiron.code.lineage.sql.dao.SqlLineageEdgeRepository;
import com.litiron.code.lineage.sql.dao.SqlLineageNodeRepository;
import com.litiron.code.lineage.sql.dto.ParsedTableMeta;
import com.litiron.code.lineage.sql.dto.lineage.SqlLineageTableDto;
import com.litiron.code.lineage.sql.dto.lineage.SqlLineageTableEdgeDto;
import com.litiron.code.lineage.sql.entity.SqlLineageEdgeEntity;
import com.litiron.code.lineage.sql.entity.SqlLineageNodeEntity;
import com.litiron.code.lineage.sql.service.SqlLineageService;
import com.litiron.code.lineage.sql.utils.TableNodeUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: Litiron
 * @create: 2024-06-08 10:02
 **/
@Service
@AllArgsConstructor
public class SqlLineageServiceImpl implements SqlLineageService {

    private final SqlLineageNodeRepository sqlLineageNodeRepository;
    private final SqlLineageEdgeRepository sqlLineageEdgeRepository;

    @Override
    public ParsedTableMeta parseRelationTables(String sql) {
        return obtainAllTables(sql);
    }

    /**
     * 主要是针对表的依赖关系进行解析
     * 解析之后的结果可以查看到一张表与其他表是通过什么条件进行关联的
     */
    @Override
    public void parseTableDependency(String sql) {
        List<SQLStatement> sqlStatements = parsePgStatements(sql);
        Map<String, SqlLineageTableEdgeDto> joinEdgeMap = new HashMap<>(16);
        for (SQLStatement statement : sqlStatements) {
            PGSchemaStatVisitor pgSchemaStatVisitor = new PGSchemaStatVisitor();
            statement.accept(pgSchemaStatVisitor);
            Set<TableStat.Relationship> relationships = pgSchemaStatVisitor.getRelationships();
            // 整理join关系中的表和字段关系
            for (TableStat.Relationship relationship : relationships) {
                String leftT = relationship.getLeft().getTable();
                String rightT = relationship.getRight().getTable();
                String key = TableNodeUtils.generateKey(leftT, rightT);
                SqlLineageTableEdgeDto sqlLineageTableEdgeDto = joinEdgeMap.getOrDefault(key, new SqlLineageTableEdgeDto());
                Map<String, String> joinMap = new HashMap<>(2);
                joinMap.put(relationship.getLeft().getName(), relationship.getRight().getName());
                sqlLineageTableEdgeDto.getJoinFieldList().add(joinMap);
                sqlLineageTableEdgeDto.setLeftTableName(leftT);
                sqlLineageTableEdgeDto.setRightTableName(rightT);
                joinEdgeMap.put(key, sqlLineageTableEdgeDto);
            }
            // 获取sql相关表信息
            ParsedTableMeta parsedTableMeta = obtainAllTables(sql);
            List<SqlLineageTableDto> sourceTableList = parsedTableMeta.getSourceTableList();
            List<SqlLineageNodeEntity> sqlLineageNodeEntityList = BeanUtil.copyToList(sourceTableList, SqlLineageNodeEntity.class);
            Map<String, SqlLineageNodeEntity> sqlLineageNodeEntityMap = sqlLineageNodeEntityList.stream()
                    .collect(Collectors.toMap(SqlLineageNodeEntity::getTableName, Function.identity(), (pre, next) -> pre));
            // neo4j 存储
            sqlLineageNodeEntityList.forEach(node -> {
                List<SqlLineageEdgeEntity> outEdgeList = new ArrayList<>();
                List<String> visitedKeyList = new ArrayList<>();
                joinEdgeMap.forEach((key, value) -> {
                    if (key.contains(node.getTableName())) {
                        // 先简单判断唯一键是否存在，理论上后续应该还需要更新连接字段心
                        // todo
                        SqlLineageEdgeEntity sqlLineageEdgeEntity = BeanUtil.copyProperties(value, SqlLineageEdgeEntity.class);
                        Integer edgeCount = sqlLineageEdgeRepository.countByUniqueId(sqlLineageEdgeEntity.getUniqueId());
                        if (edgeCount > 0) {
                            return;
                        }
                        sqlLineageEdgeEntity.setRelationFiled(JSONUtil.toJsonStr(value.getJoinFieldList()));
                        sqlLineageEdgeEntity.setUniqueId();
                        visitedKeyList.add(key);
                        outEdgeList.add(sqlLineageEdgeEntity);
                        // 找到边之后要将另一边的node添加至to
                        if (value.getLeftTableName().equals(node.getTableName())) {
                            sqlLineageEdgeEntity.setTo(sqlLineageNodeEntityMap.get(value.getRightTableName()));
                        } else {
                            sqlLineageEdgeEntity.setTo(sqlLineageNodeEntityMap.get(value.getLeftTableName()));
                        }
                    }
                });
                node.setOutRelationShip(outEdgeList);
                visitedKeyList.forEach(joinEdgeMap::remove);
                sqlLineageNodeRepository.save(node);
            });
        }
    }


    private List<SQLStatement> parsePgStatements(String sql) {
        return SQLUtils.parseStatements(sql, DbType.postgresql);
    }

    private ParsedTableMeta obtainAllTables(String sqlStatement) {
        ParsedTableMeta parsedTableMeta = new ParsedTableMeta();
        List<SQLStatement> sqlStatements = parsePgStatements(sqlStatement);
        List<SqlLineageTableDto> sourceTableList = new ArrayList<>();
        List<SqlLineageTableDto> destTableList = new ArrayList<>();
        for (SQLStatement statement : sqlStatements) {
            PGSchemaStatVisitor pgSchemaStatVisitor = new PGSchemaStatVisitor();
            statement.accept(pgSchemaStatVisitor);
            distinguishTableType(sourceTableList, destTableList, pgSchemaStatVisitor);
        }
        parsedTableMeta.setDestTableList(destTableList);
        parsedTableMeta.setSourceTableList(sourceTableList);
        return parsedTableMeta;
    }

    private void distinguishTableType(List<SqlLineageTableDto> selectTableList,
                                      List<SqlLineageTableDto> insertTableList,
                                      PGSchemaStatVisitor pgSchemaStatVisitor) {
        Map<TableStat.Name, TableStat> allTables = pgSchemaStatVisitor.getTables();
        allTables.forEach((k, v) -> {
            SqlLineageTableDto node = new SqlLineageTableDto();
            String fullTableName = k.getName();
            String[] split = fullTableName.split("\\.");
            if (split.length != 2) {
                throw new BusinessException("存在未携带Schema的SQL语句");
            }
            node.setTableName(fullTableName);
            node.setSchemaName(split[0]);

            if (v.getInsertCount() > 0) {
                insertTableList.add(node);
            } else {
                selectTableList.add(node);
            }
        });
    }
}
