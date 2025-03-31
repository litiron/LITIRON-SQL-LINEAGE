package com.litiron.code.lineage.sql.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.litiron.code.lineage.sql.dao.table.SqlLineageTableNodeRepository;
import com.litiron.code.lineage.sql.dto.database.DatabaseConnectionDto;
import com.litiron.code.lineage.sql.dto.database.DatabaseStructInfoDto;
import com.litiron.code.lineage.sql.dto.lineage.SqlLineageTableNodeParamsDto;
import com.litiron.code.lineage.sql.dto.lineage.table.SqlLineageTableEdgeDto;
import com.litiron.code.lineage.sql.dto.lineage.table.SqlLineageTableNodeDto;
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

    private static final String NODE_ID_PATTERN = "%s:%s:%s:%s";

    @Override
    public List<SqlLineageTableNodeDto> retrieveNeo4jTableInfo(SqlLineageTableNodeParamsDto sqlLineageTableNodeParamsDto) {
        SqlLineageTableNodeEntity target = buildSqlLineageNodeParams(sqlLineageTableNodeParamsDto);
        if (StrUtil.isEmpty(target.getSchemaName())) {
            target.setSchemaName("null");
        }
        String id = String.format(NODE_ID_PATTERN, target.getConnectionIp(), target.getDatabaseName(), target.getSchemaName(), target.getTableName());
        SqlLineageTableNodeEntity foundNode = sqlLineageNodeRepository.findNodeWithAllRelationships(id);
        return (foundNode != null) ? List.of(convertToFullDto(foundNode)) : null;
    }

    private SqlLineageTableNodeDto convertToFullDto(SqlLineageTableNodeEntity entity) {
        SqlLineageTableNodeDto dto = BeanUtil.copyProperties(entity, SqlLineageTableNodeDto.class);

        // 处理下游关系
        List<SqlLineageTableEdgeDto> downstreamEdges = entity.getOutRelationShip().stream()
                .map(edge -> {
                    SqlLineageTableEdgeDto edgeDto = BeanUtil.copyProperties(edge, SqlLineageTableEdgeDto.class);
                    edgeDto.setDirection("DOWNSTREAM");
                    return edgeDto;
                })
                .toList();

//        // 处理上游关系
//        List<SqlLineageTableEdgeDto> upstreamEdges = entity.getInRelationship().stream()
//                .map(edge -> {
//                    SqlLineageTableEdgeDto edgeDto = BeanUtil.copyProperties(edge, SqlLineageTableEdgeDto.class);
//                    edgeDto.setDirection("UPSTREAM");
//                    return edgeDto;
//                })
//                .toList();

        // 合并所有关系
        List<SqlLineageTableEdgeDto> allEdges = new ArrayList<>(downstreamEdges);
//        allEdges.addAll(upstreamEdges);

        dto.setOutgoingRelationShip(allEdges);
        return dto;
    }

    private SqlLineageTableNodeEntity buildSqlLineageNodeParams(SqlLineageTableNodeParamsDto sqlLineageTableNodeParamsDto) {
        //此处不用BeanUtil是因为会复制ID，以至于在图数据库搜不到
        SqlLineageTableNodeEntity target = new SqlLineageTableNodeEntity();
        target.setSchemaName(sqlLineageTableNodeParamsDto.getSchemaName());
        target.setTableName(sqlLineageTableNodeParamsDto.getTableName());
        target.setDatabaseName(sqlLineageTableNodeParamsDto.getDatabaseName());
        DatabaseConnectionEntity databaseConnectionInfo = databaseConnectionService.getDatabaseConnectionInfoById(sqlLineageTableNodeParamsDto.getId());
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
}