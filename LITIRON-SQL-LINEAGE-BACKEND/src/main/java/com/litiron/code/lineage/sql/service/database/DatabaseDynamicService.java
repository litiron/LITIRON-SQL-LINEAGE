package com.litiron.code.lineage.sql.service.database;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

/**
 * @description: 动态数据库service
 * @author: 李日红
 * @create: 2025/2/8 17:29
 */
public interface DatabaseDynamicService {
    /**
     * @description: 分页获取表内容信息
     * @param: page 分页参数
     * @param: tableName 表名
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<java.util.Map < java.lang.String, java.lang.Object>>
     * @author: 李日红
     * @create: 2024/12/7 13:35
     */
    IPage<Map<String, Object>> retrieveTableDetailsByPage(IPage<Map<String, Object>> page, String tableName);

    /**
     * @description: 获取表内容信息
     * @param: tableName 表名
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @author: 李日红
     * @create: 2025/4/14 17:46
     */
    Map<String, Object> retrieveTableDetails(String tableName);

}
