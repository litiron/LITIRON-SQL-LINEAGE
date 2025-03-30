package com.litiron.code.lineage.sql.dto.database;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 李日红
 * @description: 数据库连接参数Dto
 * @create 2024/12/1 14:21
 */
@Setter
@Getter
public class DatabaseConnectionParamsDto {
    private String id;

    private String ip;

    private Integer port;

    private String userName;

    private String password;

    private String connectionName;

    private String type;

    private String uId;
}
