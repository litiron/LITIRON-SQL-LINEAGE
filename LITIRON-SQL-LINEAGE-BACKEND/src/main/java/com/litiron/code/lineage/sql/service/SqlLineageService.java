package com.litiron.code.lineage.sql.service;

import com.litiron.code.lineage.sql.dto.ParsedTableMeta;
import com.litiron.code.lineage.sql.dto.lineage.ParseRelationParamsDto;

/**
 * @description: sql 血缘关系相关服务
 * @author: Litiron
 * @create: 2024-06-08 10:01
 **/
public interface SqlLineageService {

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

}
