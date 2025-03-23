package com.litiron.code.lineage.sql.dto.lineage;

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
    private String databaseName;
    //    private String databaseName = TableConstants.DEFAULT_DATABASE_NAME;
    //    private String connectionIp = TableConstants.DEFAULT_CONNECTION_IP;
    private String connectionIp;
    private String schemaName;
    private Integer connectionPort;
    //    private Integer connectionPort = TableConstants.DEFAULT_CONNECTION_PORT;
    private String tableComment;
}
