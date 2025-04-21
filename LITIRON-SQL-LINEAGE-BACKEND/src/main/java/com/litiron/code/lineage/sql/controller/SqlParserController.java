package com.litiron.code.lineage.sql.controller;

import cn.hutool.core.util.StrUtil;
import com.litiron.code.lineage.sql.common.BusinessException;
import com.litiron.code.lineage.sql.common.Rest;
import com.litiron.code.lineage.sql.dto.ParsedTableMeta;
import com.litiron.code.lineage.sql.dto.lineage.ParseRelationParamsDto;
import com.litiron.code.lineage.sql.dto.lineage.column.ParsedColumnMetaDto;
import com.litiron.code.lineage.sql.service.SqlLineageParseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: sql解析控制器
 * @author: Litiron
 * @create: 2024-06-08 08:52
 **/
@RequestMapping("/sql")
@RestController
public class SqlParserController {

    private SqlLineageParseService sqlLineageParseService;

    @PostMapping("/parse/relation/table")
    public Rest<ParsedTableMeta> parseRelationTables(@RequestBody ParseRelationParamsDto parseRelationParamsDto) {
        validateParseParams(parseRelationParamsDto);
        ParsedTableMeta parsedTableMeta = sqlLineageParseService.parseRelationTables(parseRelationParamsDto);
        return Rest.success(parsedTableMeta);
    }

    @PostMapping("/parse/table/dependency")
    public Rest<?> parseTableDependency(@RequestBody ParseRelationParamsDto parseRelationParamsDto) {
        validateParseParams(parseRelationParamsDto);
        sqlLineageParseService.parseTableDependency(parseRelationParamsDto);
        return Rest.success("添加成功");
    }

    @PostMapping("/parse/relation/column")
    public Rest<List<ParsedColumnMetaDto>> parseRelationColumn(@RequestBody ParseRelationParamsDto parseRelationParamsDto) {
        validateParseParams(parseRelationParamsDto);
        return Rest.success(sqlLineageParseService.parseColumnRelation(parseRelationParamsDto));
    }

    @PostMapping("/parse/column/dependency")
    public Rest<?> parseColumnDependency(@RequestBody ParseRelationParamsDto parseRelationParamsDto) {
        validateParseParams(parseRelationParamsDto);
        sqlLineageParseService.parseColumnDependency(parseRelationParamsDto);
        return Rest.success();
    }

    @PostMapping("/truncate/dependency")
    public Rest<?> truncateDependency() {
        sqlLineageParseService.truncateDependency();
        return Rest.success("删除成功");
    }

    private void validateParseParams(ParseRelationParamsDto parseRelationParamsDto) {
        if (StrUtil.isEmpty(parseRelationParamsDto.getSql())) {
            throw new BusinessException("解析sql不能为空");
        }
        if (StrUtil.isEmpty(parseRelationParamsDto.getConnectionId())) {
            throw new BusinessException("连接ID不能为空");
        }
    }

    @Autowired
    public void setSqlLineageService(SqlLineageParseService sqlLineageParseService) {
        this.sqlLineageParseService = sqlLineageParseService;
    }
}
