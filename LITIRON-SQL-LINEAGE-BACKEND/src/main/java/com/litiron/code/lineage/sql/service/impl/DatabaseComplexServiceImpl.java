package com.litiron.code.lineage.sql.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litiron.code.lineage.sql.constants.DatabaseConnectionConstant;
import com.litiron.code.lineage.sql.dto.database.*;
import com.litiron.code.lineage.sql.entity.database.DatabaseConnectionEntity;
import com.litiron.code.lineage.sql.holder.DBContextHolder;
import com.litiron.code.lineage.sql.holder.DynamicDataSource;
import com.litiron.code.lineage.sql.service.DatabaseComplexService;
import com.litiron.code.lineage.sql.service.database.DatabaseConnectionService;
import com.litiron.code.lineage.sql.service.database.DatabaseDynamicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 数据库service操作实现类
 * @author: 李日红
 * @create: 2024/11/30 13:59
 */
@Service
@Slf4j
public class DatabaseComplexServiceImpl implements DatabaseComplexService {
    private DatabaseConnectionService databaseConnectionService;

    private DatabaseDynamicService databaseDynamicService;
    private DynamicDataSource dynamicDataSource;

    @Override
    public List<DatabaseConnectionDto> retrieveDatabaseConnectionInfo() {
        List<DatabaseConnectionEntity> databaseConnectionEntities = databaseConnectionService.getaAllDatabaseConnectionInfo();
        return BeanUtil.copyToList(databaseConnectionEntities, DatabaseConnectionDto.class);
    }

    @Override
    public List<DatabaseStructInfoDto> updateDatabaseConnection(String id,String pgDbName) {
        DatabaseConnectionEntity testConnectionEntity = databaseConnectionService.getDatabaseConnectionInfoById(id);
        DatabaseMetaData metaData = dynamicDataSource.createDataSourceWithCheck(testConnectionEntity,pgDbName);
        List<DatabaseStructInfoDto> databaseStructInfos = retrieveDatabaseStructInfo(metaData, testConnectionEntity.getType());
        DBContextHolder.setDataSource(id);
        DBContextHolder.clearDataSource();
        return databaseStructInfos;
    }

    @Override
    public IPage<Map<String, Object>> retrieveTableDetails(QueryTableDetailsParamsDto queryTableDetailsParamsDto) {
        DBContextHolder.setDataSource(queryTableDetailsParamsDto.getConnectionId());
        IPage<Map<String, Object>> page = new Page<>(queryTableDetailsParamsDto.getPageNumber(), queryTableDetailsParamsDto.getPageSize());
        IPage<Map<String, Object>> tableDetails = databaseDynamicService.retrieveTableDetails(page, queryTableDetailsParamsDto.getTableName());
        DBContextHolder.clearDataSource();
        return tableDetails;
    }

    @Override
    public List<String> retrievePgDatabasesInfo(String id) {
        DatabaseConnectionEntity testConnectionEntity = databaseConnectionService.getDatabaseConnectionInfoById(id);
        return dynamicDataSource.getPgDatabases(testConnectionEntity);
    }

    private List<DatabaseStructInfoDto> retrieveDatabaseStructInfo(DatabaseMetaData metaData, String type) {
        List<DatabaseStructInfoDto> databaseStructInfos = new ArrayList<>();
        try {
            if (type.equals(DatabaseConnectionConstant.CONNECTION_TYPE_MYSQL)) {
                ResultSet catalogRet = metaData.getCatalogs();
                databaseStructInfos = buildDatabaseInfo(catalogRet, metaData, type);
            } else if (type.equals(DatabaseConnectionConstant.CONNECTION_TYPE_PGSQL)) {
                ResultSet databaseRet = metaData.getSchemas();
                databaseStructInfos = buildDatabaseInfo(databaseRet, metaData, type);
            }
        } catch (Exception e) {
            log.error("RetrieveDatabaseStructInfo is error");
        }
        return databaseStructInfos;
    }

    private List<DatabaseStructInfoDto> buildDatabaseInfo(ResultSet databaseRet, DatabaseMetaData metaData, String type) {
        List<DatabaseStructInfoDto> databaseStructInfos = new ArrayList<>();
        try {
            while (databaseRet.next()) {
                String databaseName = "";
                if (type.equals(DatabaseConnectionConstant.CONNECTION_TYPE_MYSQL)) {
                    databaseName = databaseRet.getString("TABLE_CAT");

                } else if (type.equals(DatabaseConnectionConstant.CONNECTION_TYPE_PGSQL)) {
                    databaseName = databaseRet.getString("TABLE_SCHEM");
                }
                DatabaseStructInfoDto databaseDto = new DatabaseStructInfoDto();
                databaseDto.setDatabaseName(databaseName);
                List<TableStructureInfoDto> tableStructureInfoDtoList = buildTableInfo(databaseName, metaData, type);
                databaseDto.setTableStructureInfoDtoList(tableStructureInfoDtoList);
                databaseStructInfos.add(databaseDto);
            }
        } catch (Exception e) {
            log.error("BuildDatabaseInfo is error", e);
        }
        return databaseStructInfos;
    }

    private List<TableStructureInfoDto> buildTableInfo(String databaseName, DatabaseMetaData metaData, String type) {
        List<TableStructureInfoDto> tableStructureInfoDtoList = new ArrayList<>();
        try {
            ResultSet tableRet = null;
            if (type.equals(DatabaseConnectionConstant.CONNECTION_TYPE_MYSQL)) {
                tableRet = metaData.getTables(databaseName, "%", "%",
                        new String[]{"TABLE"});
            } else if (type.equals(DatabaseConnectionConstant.CONNECTION_TYPE_PGSQL)) {
                tableRet = metaData.getTables(null, databaseName, "%",
                        new String[]{"TABLE"});
            }

            while (tableRet.next()) {
                TableStructureInfoDto tableDto = new TableStructureInfoDto();
                String tableName = tableRet.getString("TABLE_NAME");
                String tableComment = tableRet.getString("REMARKS");
                tableDto.setTableName(tableName);
                tableDto.setTableComment(tableComment);
                List<ColumnStructureInfoDto> columnStructureInfoDtoList = buildColumnInfo(databaseName, tableName, metaData);
                tableDto.setColumnStructureInfoDtoList(columnStructureInfoDtoList);
                tableStructureInfoDtoList.add(tableDto);
            }
        } catch (Exception e) {
            log.error("BuildTableInfo is error", e);
        }
        return tableStructureInfoDtoList;
    }

    private List<ColumnStructureInfoDto> buildColumnInfo(String databaseName, String tableName, DatabaseMetaData metaData) {
        List<ColumnStructureInfoDto> columnStructureInfoDtoList = new ArrayList<>();
        try {
            ResultSet columnRet = metaData.getColumns(databaseName, "%", tableName, "%");
            while (columnRet.next()) {
                ColumnStructureInfoDto columnStructureInfoDto = new ColumnStructureInfoDto();
                String columnName = columnRet.getString("COLUMN_NAME");
                String columnComment = columnRet.getString("REMARKS");
                columnStructureInfoDto.setColumnName(columnName);
                columnStructureInfoDto.setColumnComment(columnComment);
                columnStructureInfoDtoList.add(columnStructureInfoDto);
            }
        } catch (Exception e) {
            log.error("BuildColumnInfo is error", e);
        }
        return columnStructureInfoDtoList;
    }


    @Autowired
    public void setDatabaseConnectionService(DatabaseConnectionService databaseConnectionService) {
        this.databaseConnectionService = databaseConnectionService;
    }

    @Autowired
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    @Autowired
    public void setDatabaseDynamicService(DatabaseDynamicService databaseDynamicService) {
        this.databaseDynamicService = databaseDynamicService;
    }

}
