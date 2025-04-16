package com.litiron.code.lineage.sql.service.impl;

import com.litiron.code.lineage.sql.dto.ParsedTableMeta;
import com.litiron.code.lineage.sql.dto.lineage.ParseRelationParamsDto;
import com.litiron.code.lineage.sql.dto.lineage.column.ParsedColumnMetaDto;
import com.litiron.code.lineage.sql.service.SqlLineageParseService;
import com.litiron.code.lineage.sql.service.column.SqlLineageColumnService;
import com.litiron.code.lineage.sql.service.table.SqlLineageTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: sql血缘解析服务管管
 * @author: Litiron
 * @create: 2024-06-08 10:02
 **/
@Service
@Slf4j
public class SqlLineageParseServiceImpl implements SqlLineageParseService {

    private final SqlLineageTableService sqlLineageTableService;

    private final SqlLineageColumnService sqlLineageColumnService;


    public SqlLineageParseServiceImpl(SqlLineageTableService sqlLineageTableService, SqlLineageColumnService sqlLineageColumnService) {
        this.sqlLineageTableService = sqlLineageTableService;
        this.sqlLineageColumnService = sqlLineageColumnService;
    }

    @Override
    public ParsedTableMeta parseRelationTables(ParseRelationParamsDto parseRelationParamsDto) {
        return sqlLineageTableService.parseRelationTables(parseRelationParamsDto);
    }

    /**
     * 主要是针对表的依赖关系进行解析
     * 解析之后的结果可以查看到一张表与其他表是通过什么条件进行关联的
     */
    @Override
    public void parseTableDependency(ParseRelationParamsDto parseRelationParamsDto) {
        sqlLineageTableService.parseTableDependency(parseRelationParamsDto);
    }

    @Override
    public void parseColumnDependency(ParseRelationParamsDto parseRelationParamsDto) {
        sqlLineageColumnService.parseColumnDependency(parseRelationParamsDto);
    }


    @Override
    public List<ParsedColumnMetaDto> parseColumnRelation(ParseRelationParamsDto parseRelationParamsDto) {
        return sqlLineageColumnService.parseColumnRelation(parseRelationParamsDto);
    }

    @Override
    public void truncateDependency() {
        sqlLineageTableService.truncateDependency();
    }

}
