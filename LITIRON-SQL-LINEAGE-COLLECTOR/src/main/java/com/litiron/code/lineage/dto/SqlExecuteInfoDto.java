package com.litiron.code.lineage.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @description: sql执行中的信息
 * @author: Litiron
 * @create: 2025-04-12 14:21
 **/
@Getter
@Setter
@Builder
public class SqlExecuteInfoDto {

    /**
     * SQL语句
     */
    private String sql;
    /**
     * 数据库类型
     */
    private String databaseType;
    /**
     * 数据库名称
     */
    private String databaseName;
    /**
     * 数据库主机
     */
    private String databaseHost;
    /**
     * 数据库端口
     */
    private Integer databasePort;
    /**
     * SQL执行时间（不是持续时间）
     */
    private Long executeTime;
    /**
     * SQL来源于哪个模块
     */
    private String applicationName;
}
