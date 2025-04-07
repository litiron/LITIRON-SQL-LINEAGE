package com.litiron.code.lineage.sql.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @description: 动态获取数据库表的内容
 * @author: 李日红
 * @create: 2024/12/11 22:19
 */

@Mapper
public interface DatabaseDynamicRepository {

    /**
     * @description: 获取表内容信息
     * @param: tableName 表名
     * @return: java.util.List<Map < String, Object>>
     * @author: 李日红
     * @create: 2024/12/7 13:36
     */
    IPage<Map<String, Object>> retrieveTableDetails(IPage<Map<String, Object>> page,
                                                    @Param(value = "tableName") String tableName);
}
