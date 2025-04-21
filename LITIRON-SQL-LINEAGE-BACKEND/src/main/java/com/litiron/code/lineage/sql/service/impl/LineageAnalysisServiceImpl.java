package com.litiron.code.lineage.sql.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.litiron.code.lineage.sql.dao.column.SqlLineageColumnRepository;
import com.litiron.code.lineage.sql.dao.table.SqlLineageTableNodeRepository;
import com.litiron.code.lineage.sql.dto.database.DatabaseConnectionDto;
import com.litiron.code.lineage.sql.dto.database.DatabaseStructInfoDto;
import com.litiron.code.lineage.sql.dto.lineage.SqlLineageSearchNodeParamsDto;
import com.litiron.code.lineage.sql.dto.lineage.column.SqlLineageColumnEdgeDto;
import com.litiron.code.lineage.sql.dto.lineage.column.SqlLineageColumnNodeDto;
import com.litiron.code.lineage.sql.dto.lineage.table.SqlLineageTableEdgeDto;
import com.litiron.code.lineage.sql.dto.lineage.table.SqlLineageTableNodeDto;
import com.litiron.code.lineage.sql.entity.column.SqlLineageColumnNodeEntity;
import com.litiron.code.lineage.sql.entity.database.DatabaseConnectionEntity;
import com.litiron.code.lineage.sql.entity.table.SqlLineageTableNodeEntity;
import com.litiron.code.lineage.sql.service.LineageAnalysisService;
import com.litiron.code.lineage.sql.service.database.DatabaseConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李日红
 * @description: 血缘关系Service实现类
 * @create 2025/2/8 17:48
 */
@Service
public class LineageAnalysisServiceImpl implements LineageAnalysisService {
    private DatabaseConnectionService databaseConnectionService;
    private DatabaseComplexServiceImpl databaseComplexService;
    private SqlLineageTableNodeRepository sqlLineageNodeRepository;

    private SqlLineageColumnRepository sqlLineageColumnRepository;

    @Override
    public List<String> retrieveDatabaseType() {
        return databaseConnectionService.getAllDatabaseType();
    }

    @Override
    public List<DatabaseConnectionDto> retrieveAllConnection() {
        return databaseComplexService.retrieveDatabaseConnectionInfo();
    }

    @Override
    public List<DatabaseStructInfoDto> updateDatabaseConnection(String id, String pgDbName) {
        return databaseComplexService.updateDatabaseConnection(id, pgDbName);
    }

    private static final String TABLE_NODE_ID_PATTERN = "%s:%s:%s:%s";
    private static final String COLUMN_NODE_ID_PATTERN = "%s:%s:%s:%s:%s";

    @Override
    public List<SqlLineageTableNodeDto> retrieveNeo4jTableInfo(SqlLineageSearchNodeParamsDto sqlLineageSearchNodeParamsDto) {
        SqlLineageTableNodeEntity target = buildSqlLineageTableNodeParams(sqlLineageSearchNodeParamsDto);
        if (StrUtil.isEmpty(target.getSchemaName())) {
            target.setSchemaName("null");
        }
        String id = String.format(TABLE_NODE_ID_PATTERN, target.getConnectionIp(), target.getDatabaseName(), target.getSchemaName(), target.getTableName());
        SqlLineageTableNodeEntity foundNode = sqlLineageNodeRepository.findNodeWithAllRelationships(id);
        return (foundNode != null) ? List.of(convertToFullTableDto(foundNode)) : null;
    }

    @Override
    public List<SqlLineageColumnNodeDto> retrieveNeo4jColumnInfo(SqlLineageSearchNodeParamsDto sqlLineageSearchNodeParamsDto) {
        SqlLineageColumnNodeEntity target = buildSqlLineageColumnNodeParams(sqlLineageSearchNodeParamsDto);
        if (StrUtil.isEmpty(target.getSchemaName())) {
            target.setSchemaName("null");
        }
        String id = String.format(COLUMN_NODE_ID_PATTERN, target.getConnectionIp(), target.getDatabaseName(), target.getSchemaName(), target.getTableName(), target.getColumnName());
        SqlLineageColumnNodeEntity foundNode = sqlLineageColumnRepository.findNodeWithAllRelationships(id);
        return (foundNode != null) ? List.of(convertToFullColumnDto(foundNode)) : null;
    }

    private SqlLineageColumnNodeDto convertToFullColumnDto(SqlLineageColumnNodeEntity entity) {
        SqlLineageColumnNodeDto dto = BeanUtil.copyProperties(entity, SqlLineageColumnNodeDto.class);
        // 处理下游关系
        List<SqlLineageColumnEdgeDto> downstreamEdges = entity.getOutRelationShip().stream()
                .map(edge -> {
                    SqlLineageColumnEdgeDto edgeDto = BeanUtil.copyProperties(edge, SqlLineageColumnEdgeDto.class);
                    edgeDto.setDirection("DOWNSTREAM");
                    return edgeDto;
                })
                .toList();

        // 处理上游关系
        List<SqlLineageColumnEdgeDto> upstreamEdges = entity.getInRelationship().stream()
                .map(edge -> {
                    SqlLineageColumnEdgeDto edgeDto = BeanUtil.copyProperties(edge, SqlLineageColumnEdgeDto.class);
                    edgeDto.setDirection("UPSTREAM");
                    return edgeDto;
                })
                .toList();

        // 合并所有关系
        List<SqlLineageColumnEdgeDto> allEdges = new ArrayList<>(downstreamEdges);
        allEdges.addAll(upstreamEdges);

        dto.setOutgoingRelationShip(allEdges);
        return dto;
    }

    private SqlLineageColumnNodeEntity buildSqlLineageColumnNodeParams(SqlLineageSearchNodeParamsDto sqlLineageSearchNodeParamsDto) {
        DatabaseConnectionEntity databaseConnectionInfo = databaseConnectionService.getDatabaseConnectionInfoById(sqlLineageSearchNodeParamsDto.getId());
        SqlLineageColumnNodeEntity target = BeanUtil.copyProperties(sqlLineageSearchNodeParamsDto, SqlLineageColumnNodeEntity.class);
        target.setId("");
        target.setPort(databaseConnectionInfo.getPort());
        target.setConnectionIp(databaseConnectionInfo.getIp());
        return target;
    }

    private SqlLineageTableNodeDto convertToFullTableDto(SqlLineageTableNodeEntity entity) {
        SqlLineageTableNodeDto dto = BeanUtil.copyProperties(entity, SqlLineageTableNodeDto.class);

        // 处理下游关系
        List<SqlLineageTableEdgeDto> downstreamEdges = entity.getOutRelationShip().stream()
                .map(edge -> {
                    SqlLineageTableEdgeDto edgeDto = BeanUtil.copyProperties(edge, SqlLineageTableEdgeDto.class);
                    edgeDto.setDirection("DOWNSTREAM");
                    return edgeDto;
                })
                .toList();

        // 处理上游关系
        List<SqlLineageTableEdgeDto> upstreamEdges = entity.getInRelationship().stream()
                .map(edge -> {
                    SqlLineageTableEdgeDto edgeDto = BeanUtil.copyProperties(edge, SqlLineageTableEdgeDto.class);
                    edgeDto.setDirection("UPSTREAM");
                    return edgeDto;
                })
                .toList();

        // 合并所有关系
        List<SqlLineageTableEdgeDto> allEdges = new ArrayList<>(downstreamEdges);
        allEdges.addAll(upstreamEdges);

        dto.setOutgoingRelationShip(allEdges);
        return dto;
    }

    private SqlLineageTableNodeEntity buildSqlLineageTableNodeParams(SqlLineageSearchNodeParamsDto sqlLineageSearchNodeParamsDto) {
        //此处不用BeanUtil是因为会复制ID，以至于在图数据库搜不到
        SqlLineageTableNodeEntity target = new SqlLineageTableNodeEntity();
        target.setSchemaName(sqlLineageSearchNodeParamsDto.getSchemaName());
        target.setTableName(sqlLineageSearchNodeParamsDto.getTableName());
        target.setDatabaseName(sqlLineageSearchNodeParamsDto.getDatabaseName());
        DatabaseConnectionEntity databaseConnectionInfo = databaseConnectionService.getDatabaseConnectionInfoById(sqlLineageSearchNodeParamsDto.getId());
        target.setConnectionIp(databaseConnectionInfo.getIp());
        target.setConnectionPort(databaseConnectionInfo.getPort());
        return target;
    }


    @Autowired
    public void setDatabaseConnectionService(DatabaseConnectionService databaseConnectionService) {
        this.databaseConnectionService = databaseConnectionService;
    }

    @Autowired
    public void setDatabaseComplexService(DatabaseComplexServiceImpl databaseComplexService) {
        this.databaseComplexService = databaseComplexService;
    }

    @Autowired
    public void setSqlLineageNodeRepository(SqlLineageTableNodeRepository sqlLineageNodeRepository) {
        this.sqlLineageNodeRepository = sqlLineageNodeRepository;
    }

    @Autowired
    public void setSqlLineageColumnRepository(SqlLineageColumnRepository sqlLineageColumnRepository) {
        this.sqlLineageColumnRepository = sqlLineageColumnRepository;
    }
}
