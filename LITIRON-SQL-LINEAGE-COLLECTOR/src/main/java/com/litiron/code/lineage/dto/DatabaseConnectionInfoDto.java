package com.litiron.code.lineage.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @description: 数据库连接信息
 * @author: Litiron
 * @create: 2025-04-12 14:40
 **/
@Getter
@Setter
@Builder
public class DatabaseConnectionInfoDto {

    private String url;

    private String username;

    private String password;

    private String driver;

    private String databaseType;

    private String databaseName;

    private String host;

    private Integer port;
}
