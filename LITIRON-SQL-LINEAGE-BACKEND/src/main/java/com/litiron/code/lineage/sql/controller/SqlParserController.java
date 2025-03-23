package com.litiron.code.lineage.sql.controller;

import cn.hutool.core.util.StrUtil;
import com.litiron.code.lineage.sql.common.BusinessException;
import com.litiron.code.lineage.sql.common.Rest;
import com.litiron.code.lineage.sql.dao.SqlLineageNodeRepository;
import com.litiron.code.lineage.sql.dto.ParsedTableMeta;
import com.litiron.code.lineage.sql.dto.lineage.ParseRelationParamsDto;
import com.litiron.code.lineage.sql.service.SqlLineageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description: sql解析控制器
 * @author: Litiron
 * @create: 2024-06-08 08:52
 **/
@RequestMapping("/sql")
@RestController
public class SqlParserController {

    private SqlLineageService sqlLineageService;
    @Autowired
    private SqlLineageNodeRepository sqlLineageNodeRepository;

    @PostMapping("/parse/relation/table")
    public Rest<ParsedTableMeta> parseRelationTables(@RequestBody ParseRelationParamsDto parseRelationParamsDto) {
        validateParseParams(parseRelationParamsDto);
        ParsedTableMeta parsedTableMeta = sqlLineageService.parseRelationTables(parseRelationParamsDto);
        return Rest.success(parsedTableMeta);
    }

    @PostMapping("/parse/table/dependency")
    public Rest<ParsedTableMeta> parseTableDependency(@RequestBody ParseRelationParamsDto parseRelationParamsDto) {
        validateParseParams(parseRelationParamsDto);
        sqlLineageService.parseTableDependency(parseRelationParamsDto);
        return Rest.success("添加成功");
    }

    @PostMapping("/test/delete/node")
    public Rest<?> delGraphNode(@RequestParam(value = "id") String id) {
        sqlLineageNodeRepository.deleteById(id);
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
    public void setSqlLineageService(SqlLineageService sqlLineageService) {
        this.sqlLineageService = sqlLineageService;
    }
}
