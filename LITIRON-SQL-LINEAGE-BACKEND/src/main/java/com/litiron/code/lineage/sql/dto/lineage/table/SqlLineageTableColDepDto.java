package com.litiron.code.lineage.sql.dto.lineage.table;

import com.litiron.code.lineage.sql.dto.lineage.column.SqlLineageColumnDependencyDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @description: table中列名维度的信息
 * @author: Litiron
 * @create: 2025-03-29 19:14
 **/
@Getter
@Setter
public class SqlLineageTableColDepDto extends SqlLineageTableBaseDto {

    private String alias;
    // 存储子查询中的字段映射
    private Map<String, SqlLineageColumnDependencyDto> columnMap;
    private boolean isSubQuery;

    public SqlLineageTableColDepDto schemaName(String schemaName) {
        super.setSchemaName(schemaName);
        return this;
    }

    public SqlLineageTableColDepDto tableName(String tableName) {
        super.setTableName(tableName);
        return this;
    }

    public SqlLineageTableColDepDto alias(String alias) {
        this.alias = alias;
        return this;
    }

    public SqlLineageTableColDepDto columnMap(Map<String, SqlLineageColumnDependencyDto> columnMap) {
        this.columnMap = columnMap;
        return this;
    }

    public SqlLineageTableColDepDto isSubQuery(boolean isSubQuery) {
        this.isSubQuery = isSubQuery;
        return this;
    }

    public boolean isSubQuery() {
        return isSubQuery;
    }

    public SqlLineageTableColDepDto setSubQuery(boolean subQuery) {
        isSubQuery = subQuery;
        return this;
    }

    public SqlLineageTableColDepDto setParentTableName(String tableName) {
        super.setTableName(tableName);
        return this;
    }

    public SqlLineageTableColDepDto setParentDatabase(String databaseName) {
        super.setDatabaseName(databaseName);
        return this;
    }
}
