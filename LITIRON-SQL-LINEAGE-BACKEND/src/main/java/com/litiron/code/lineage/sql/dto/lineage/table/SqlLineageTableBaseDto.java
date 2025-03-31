package com.litiron.code.lineage.sql.dto.lineage.table;

import com.litiron.code.lineage.sql.common.constants.TableConstants;
import lombok.Getter;
import lombok.Setter;

/**
 * @description: 数据库表的基本信息
 * @author: Litiron
 * @create: 2025-03-08 16:19
 **/
@Getter
@Setter
public class SqlLineageTableBaseDto {

    private String tableName;

    private String databaseName = TableConstants.DEFAULT_DATABASE_NAME;

    private String connectionIp = TableConstants.DEFAULT_CONNECTION_IP;

    private String schemaName = TableConstants.DEFAULT_SCHEMA_NAME;

    private Integer connectionPort = TableConstants.DEFAULT_CONNECTION_PORT;

    private String tableComment;
}
