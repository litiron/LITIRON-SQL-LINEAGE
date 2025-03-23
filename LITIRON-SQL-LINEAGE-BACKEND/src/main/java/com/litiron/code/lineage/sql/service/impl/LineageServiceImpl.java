package com.litiron.code.lineage.sql.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.litiron.code.lineage.sql.dao.SqlLineageNodeRepository;
import com.litiron.code.lineage.sql.dto.database.DatabaseConnectionDto;
import com.litiron.code.lineage.sql.dto.database.DatabaseStructInfoDto;
import com.litiron.code.lineage.sql.dto.lineage.SqlLineageTableNodeDto;
import com.litiron.code.lineage.sql.dto.lineage.SqlLineageTableNodeParamsDto;
import com.litiron.code.lineage.sql.entity.SqlLineageNodeEntity;
import com.litiron.code.lineage.sql.entity.database.DatabaseConnectionEntity;
import com.litiron.code.lineage.sql.service.LineageService;
import com.litiron.code.lineage.sql.service.database.DatabaseConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 李日红
 * @description: 血缘关系Service实现类
 * @create 2025/2/8 17:48
 */
@Service
public class LineageServiceImpl implements LineageService {
    private DatabaseConnectionService databaseConnectionService;
    private DatabaseComplexServiceImpl databaseComplexService;
    private SqlLineageNodeRepository sqlLineageNodeRepository;

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

    @Override
    public List<SqlLineageTableNodeDto> retrieveNeo4jTableInfo(SqlLineageTableNodeParamsDto sqlLineageTableNodeParamsDto) {
        SqlLineageNodeEntity target = buildSqlLineageNodeParams(sqlLineageTableNodeParamsDto);
        ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.DEFAULT).withIgnorePaths("schemaName");
        List<SqlLineageNodeEntity> foundNode = sqlLineageNodeRepository.findAll(Example.of(target, exampleMatcher));
        if (foundNode.isEmpty()) {
            return null;
        }
        return buildSqlLineageTableNodeDtos(foundNode);
    }

    private SqlLineageNodeEntity buildSqlLineageNodeParams(SqlLineageTableNodeParamsDto sqlLineageTableNodeParamsDto) {
        //此处不用BeanUtil是因为会复制ID，以至于在图数据库搜不到
        SqlLineageNodeEntity target = new SqlLineageNodeEntity();
        target.setSchemaName(sqlLineageTableNodeParamsDto.getSchemaName());
        target.setTableName(sqlLineageTableNodeParamsDto.getTableName());
        target.setDatabaseName(sqlLineageTableNodeParamsDto.getDatabaseName());
        DatabaseConnectionEntity databaseConnectionInfo = databaseConnectionService.getDatabaseConnectionInfoById(sqlLineageTableNodeParamsDto.getId());
        target.setConnectionIp(databaseConnectionInfo.getIp());
        target.setConnectionPort(databaseConnectionInfo.getPort());
        return target;
    }

    private List<SqlLineageTableNodeDto> buildSqlLineageTableNodeDtos(List<SqlLineageNodeEntity> foundNode) {
        return BeanUtil.copyToList(foundNode, SqlLineageTableNodeDto.class);
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
    public void setSqlLineageNodeRepository(SqlLineageNodeRepository sqlLineageNodeRepository) {
        this.sqlLineageNodeRepository = sqlLineageNodeRepository;
    }
}
