package com.litiron.code.lineage.sql.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
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
import com.litiron.code.lineage.sql.dto.database.DatabaseStructInfoDto;
import com.litiron.code.lineage.sql.dto.database.TableStructureInfoDto;
import com.litiron.code.lineage.sql.dto.lineage.ParseRelationParamsDto;
import com.litiron.code.lineage.sql.dto.lineage.SqlLineageTableDto;
import com.litiron.code.lineage.sql.dto.lineage.SqlLineageTableEdgeDto;
import com.litiron.code.lineage.sql.entity.SqlLineageEdgeEntity;
import com.litiron.code.lineage.sql.entity.SqlLineageNodeEntity;
import com.litiron.code.lineage.sql.entity.database.DatabaseConnectionEntity;
import com.litiron.code.lineage.sql.service.SqlLineageService;
import com.litiron.code.lineage.sql.service.database.DatabaseConnectionService;
import com.litiron.code.lineage.sql.utils.TableNodeUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final DatabaseComplexServiceImpl databaseComplexService;
    private final DatabaseConnectionService databaseConnectionService;

    @Override
    public ParsedTableMeta parseRelationTables(ParseRelationParamsDto parseRelationParamsDto) {
        return obtainAllTables(parseRelationParamsDto);
    }

    /**
     * 主要是针对表的依赖关系进行解析
     * 解析之后的结果可以查看到一张表与其他表是通过什么条件进行关联的
     */
    @Override
    public void parseTableDependency(ParseRelationParamsDto parseRelationParamsDto) {
        List<SQLStatement> sqlStatements = parsePgStatements(parseRelationParamsDto.getSql());
        Map<String, SqlLineageTableEdgeDto> joinEdgeMap = new HashMap<>(16);
        for (SQLStatement statement : sqlStatements) {
            PGSchemaStatVisitor pgSchemaStatVisitor = new PGSchemaStatVisitor();
            statement.accept(pgSchemaStatVisitor);
            Set<TableStat.Relationship> relationships = pgSchemaStatVisitor.getRelationships();
            // 整理join关系中的表和字段关系
            for (TableStat.Relationship relationship : relationships) {
                String leftT = relationship.getLeft().getTable().split("\\.")[1];
                String rightT = relationship.getRight().getTable().split("\\.")[1];
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
            ParsedTableMeta parsedTableMeta = obtainAllTables(parseRelationParamsDto);
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

    private ParsedTableMeta obtainAllTables(ParseRelationParamsDto parseRelationParamsDto) {
        ParsedTableMeta parsedTableMeta = new ParsedTableMeta();
        List<SQLStatement> sqlStatements = parsePgStatements(parseRelationParamsDto.getSql());
        List<SqlLineageTableDto> sourceTableList = new ArrayList<>();
        List<SqlLineageTableDto> destTableList = new ArrayList<>();
        for (SQLStatement statement : sqlStatements) {
            PGSchemaStatVisitor pgSchemaStatVisitor = new PGSchemaStatVisitor();
            statement.accept(pgSchemaStatVisitor);
            distinguishTableType(parseRelationParamsDto.getConnectionId(), parseRelationParamsDto.getPgDbName(), sourceTableList, destTableList, pgSchemaStatVisitor);
        }
        parsedTableMeta.setDestTableList(destTableList);
        parsedTableMeta.setSourceTableList(sourceTableList);
        return parsedTableMeta;
    }

    private void distinguishTableType(String connectionId, String pgDbName, List<SqlLineageTableDto> selectTableList, List<SqlLineageTableDto> insertTableList, PGSchemaStatVisitor pgSchemaStatVisitor) {
        Map<TableStat.Name, TableStat> allTables = pgSchemaStatVisitor.getTables();
        allTables.forEach((k, v) -> {
            SqlLineageTableDto node = new SqlLineageTableDto();
            String fullTableName = k.getName();
            String[] split = fullTableName.split("\\.");
            if (split.length != 2) {
                throw new BusinessException("存在未携带Schema的SQL语句");
            }
            populateSqlLineageDto(node, connectionId, pgDbName, split);
            if (v.getInsertCount() > 0) {
                insertTableList.add(node);
            } else {
                selectTableList.add(node);
            }
        });
    }

    private void populateSqlLineageDto(SqlLineageTableDto node, String connectionId, String pgDbName, String[] split) {
        List<DatabaseStructInfoDto> databaseStructInfoDtos = databaseComplexService.updateDatabaseConnection(connectionId, pgDbName);
        List<DatabaseStructInfoDto> DbList = databaseStructInfoDtos.stream().filter(db -> db.getDatabaseName().equals(split[0])).findAny().stream().toList();
        if (CollectionUtil.isEmpty(DbList)) {
            throw new BusinessException("该数据库不存在");
        }
        List<TableStructureInfoDto> tableList = DbList.get(0).getTableStructureInfoDtoList().stream().filter(table -> table.getTableName().equals(split[1])).findAny().stream().toList();
        if (CollectionUtil.isEmpty(tableList)) {
            throw new BusinessException("该表名不存在");
        }
        DatabaseConnectionEntity connectionEntity = databaseConnectionService.getDatabaseConnectionInfoById(connectionId);
        node.setConnectionIp(connectionEntity.getIp());
        node.setConnectionPort(connectionEntity.getPort());
        node.setTableComment(tableList.get(0).getTableComment());
        if (StrUtil.isBlank(pgDbName)) {
            //mysql database.table
            node.setDatabaseName(split[0]);
        } else {
            //pgsql database.schema.table
            node.setSchemaName(split[0]);
            node.setDatabaseName(pgDbName);
        }
        node.setTableName(split[1]);
    }

}
