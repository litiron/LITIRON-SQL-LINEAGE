package com.litiron.code.lineage.sql.dto.database;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 李日红
 * @description: 查询数据库连接信息DTO
 * @create 2025/3/28 0:41
 */
@Setter
@Getter
public class QueryDatabaseConnectionParamsDto {
    private Integer pageSize;

    private Integer pageNumber;

    private String type;

    private String connectionName;
}
