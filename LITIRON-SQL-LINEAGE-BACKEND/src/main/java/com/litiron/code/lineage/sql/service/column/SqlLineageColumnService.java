package com.litiron.code.lineage.sql.service.column;

/**
 * @description:
 * @author: Litiron
 * @create: 2025-03-30 19:08
 **/
public interface SqlLineageColumnService {


    /**
     * 解析出字段级别的依赖关系
     *
     * @param sql: sql语句
     * @Description: 解析sql中关联关系
     * @Author: Litiron
     * @Date: 2024/6/16 15:30
     * @return: void
     **/
    void parseColumnDependency(String sql);
}
