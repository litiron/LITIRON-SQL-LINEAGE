package com.litiron.code.lineage.sql.service.column;

import com.litiron.code.lineage.sql.dto.lineage.ParseRelationParamsDto;
import com.litiron.code.lineage.sql.dto.lineage.column.ParsedColumnMetaDto;

import java.util.List;

/**
 * @description:
 * @author: Litiron
 * @create: 2025-03-30 19:08
 **/
public interface SqlLineageColumnService {


    /**
     * 解析出字段级别的依赖关系
     *
     * @param parseRelationParamsDto: sql解析信息
     * @Description: 解析sql中关联关系
     * @Author: Litiron
     * @Date: 2024/6/16 15:30
     * @return: void
     **/
    void parseColumnDependency(ParseRelationParamsDto parseRelationParamsDto);


    /**
     * 解析出字段级别的依赖关系,不存数据库
     *
     * @param parseRelationParamsDto: sql解析信息
     * @Description: 解析sql中关联关系
     * @Author: Litiron
     * @Date: 2024/6/16 15:30
     * @return: java.util.List<com.litiron.code.lineage.sql.dto.lineage.ParseRelationParamsDto>
     **/
    List<ParsedColumnMetaDto> parseColumnRelation(ParseRelationParamsDto parseRelationParamsDto);
}
