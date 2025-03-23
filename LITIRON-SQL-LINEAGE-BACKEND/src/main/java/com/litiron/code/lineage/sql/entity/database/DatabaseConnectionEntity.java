package com.litiron.code.lineage.sql.entity.database;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 李日红
 * @description: 数据库连接信息
 * @create 2024/12/1 14:06
 */
@Getter
@Setter
@TableName(value = "database_connection")
public class DatabaseConnectionEntity {
    private String id;
    private String ip;
    private Integer port;
    private String username;
    private String password;
    @TableField(value = "connection_name")
    private String connectionName;
    private String type;
}
