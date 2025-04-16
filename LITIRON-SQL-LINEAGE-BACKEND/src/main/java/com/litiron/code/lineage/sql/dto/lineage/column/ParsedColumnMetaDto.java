package com.litiron.code.lineage.sql.dto.lineage.column;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @description: SQL 解析 列名元数据
 * @author: Litiron
 * @create: 2025-04-16 23:54
 **/
@Getter
@Setter
@Builder
public class ParsedColumnMetaDto {

    private String databaseName;

    private String schemaName;

    private String tableName;

    private String tableComment;

    private String columnName;

    private String columnType;

    private String columnComment;
}
