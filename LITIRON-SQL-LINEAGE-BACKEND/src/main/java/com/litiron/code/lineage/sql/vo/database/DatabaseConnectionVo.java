package com.litiron.code.lineage.sql.vo.database;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 李日红
 * @description: 数据库连接信息Vo
 * @create 2024/12/1 14:14
 */
@Getter
@Setter
public class DatabaseConnectionVo {
    private String id;

    private String ip;

    private Integer port;

    private String userName;

    private String password;

    private String connectionName;

    private String type;

    private String uId;
}
