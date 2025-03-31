package com.litiron.code.lineage.sql.service.table;

import com.litiron.code.lineage.sql.dto.ParsedTableMeta;
import com.litiron.code.lineage.sql.dto.lineage.ParseRelationParamsDto;
import com.litiron.code.lineage.sql.entity.table.SqlLineageTableNodeEntity;

/**
 * @description: 表级别服务接口定义
 * @author: Litiron
 * @create: 2025-03-30 19:09
 **/
public interface SqlLineageTableService {


    /**
     * 解析出依赖的表信息
     *
     * @param parseRelationParamsDto: 解析参数
     * @Description: 解析sql中涉及的表信息
     * @Author: Litiron
     * @Date: 2024/6/15 19:32
     * @return: com.litiron.code.lineage.sql.dto.ParsedTableMeta
     **/
    ParsedTableMeta parseRelationTables(ParseRelationParamsDto parseRelationParamsDto);


    /**
     * 解析出表级别的依赖关系
     *
     * @param parseRelationParamsDto: 解析参数
     * @Description: 解析sql中关联关系
     * @Author: Litiron
     * @Date: 2024/6/16 15:30
     * @return: void
     **/
    void parseTableDependency(ParseRelationParamsDto parseRelationParamsDto);

    /**
     * 清空血缘关系表
     */
    void truncateDependency();

    /**
     * 根据表节点id查询表相关的数据信息
     *
     * @param id 表节点id
     * @return 表节点信息
     */
    SqlLineageTableNodeEntity findNodeWithAllRelationships(String id);
}
