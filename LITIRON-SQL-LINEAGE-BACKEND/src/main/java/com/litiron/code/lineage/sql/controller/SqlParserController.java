package com.litiron.code.lineage.sql.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.litiron.code.lineage.sql.bo.ParseRelationParamsBo;
import com.litiron.code.lineage.sql.common.Rest;
import com.litiron.code.lineage.sql.dao.SqlLineageNodeRepository;
import com.litiron.code.lineage.sql.dto.ParsedTableMeta;
import com.litiron.code.lineage.sql.entity.SqlLineageEdgeEntity;
import com.litiron.code.lineage.sql.entity.SqlLineageNodeEntity;
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
    public Rest<ParsedTableMeta> parseRelationTables(@RequestBody ParseRelationParamsBo parseRelationParamsBo) {
        ParsedTableMeta parsedTableMeta = sqlLineageService.parseRelationTables(parseRelationParamsBo.getSql());
        return Rest.success(parsedTableMeta);
    }

    @PostMapping("/parse/table/dependency")
    public Rest<ParsedTableMeta> parseTableDependency(@RequestBody ParseRelationParamsBo parseRelationParamsBo) {
        sqlLineageService.parseTableDependency(parseRelationParamsBo.getSql());
        return Rest.success();
    }

    @PostMapping("/test/delete/node")
    public Rest<?> delGraphNode(@RequestParam(value = "id") String id) {
        sqlLineageNodeRepository.deleteById(id);
        return Rest.success("添加成功");
    }


    @Autowired
    public void setSqlLineageService(SqlLineageService sqlLineageService) {
        this.sqlLineageService = sqlLineageService;
    }
}
