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
    private Integer id;
    private String connectionName;
    private String type;
}
