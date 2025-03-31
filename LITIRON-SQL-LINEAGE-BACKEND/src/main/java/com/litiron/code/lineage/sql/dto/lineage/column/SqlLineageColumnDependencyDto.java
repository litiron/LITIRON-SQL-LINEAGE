package com.litiron.code.lineage.sql.dto.lineage.column;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @description: 解析过车个闹钟功能列名所需要的参数
 * @author: Litiron
 * @create: 2025-03-29 19:10
 **/
@Getter
@Setter
@Builder
public class SqlLineageColumnDependencyDto {
    private String sourceSchema;
    private String sourceTable;
    private String sourceColumn;
    private String targetColumn;
    private String expression;
    // 用于存储中间别名（子查询）
    private String intermediateAlias;
}
