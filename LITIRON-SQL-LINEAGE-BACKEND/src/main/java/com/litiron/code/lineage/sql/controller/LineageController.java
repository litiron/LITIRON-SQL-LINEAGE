package com.litiron.code.lineage.sql.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.litiron.code.lineage.sql.common.BusinessException;
import com.litiron.code.lineage.sql.common.Rest;
import com.litiron.code.lineage.sql.dto.database.DatabaseConnectionDto;
import com.litiron.code.lineage.sql.dto.database.DatabaseStructInfoDto;
import com.litiron.code.lineage.sql.dto.lineage.SqlLineageTableNodeDto;
import com.litiron.code.lineage.sql.dto.lineage.SqlLineageTableNodeParamsDto;
import com.litiron.code.lineage.sql.service.LineageService;
import com.litiron.code.lineage.sql.vo.database.DatabaseConnectionVo;
import com.litiron.code.lineage.sql.vo.database.DatabaseStructInfoVo;
import com.litiron.code.lineage.sql.vo.lineage.SqlLineageTableNodeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 李日红
 * @description: 血缘关系查询控制器
 * @create 2025/2/8 17:21
 */
@RestController
@RequestMapping("/lineage")
@Slf4j
public class LineageController {

    private LineageService lineageService;

    @GetMapping("/retrieve/type")
    public Rest<List<String>> retrieveDatabaseTypeList() {
        List<String> databaseTypeList = lineageService.retrieveDatabaseType();
        return Rest.success(databaseTypeList);
    }

    @GetMapping("/retrieve/connection")
    public Rest<List<DatabaseConnectionVo>> retrieveConnectionInfo() {
        List<DatabaseConnectionDto> connectionDtos = lineageService.retrieveAllConnection();
        List<DatabaseConnectionVo> connectionVos = BeanUtil.copyToList(connectionDtos, DatabaseConnectionVo.class);
        return Rest.success(connectionVos);
    }

    @GetMapping("/update/connection")
    public Rest<?> updateDatabaseConnection(@RequestParam(value = "id") String id, @RequestParam(value = "pgDbName", required = false) String pgDbName) {
        try {
            List<DatabaseStructInfoDto> databaseStructInfoDtos = lineageService.updateDatabaseConnection(id, pgDbName);
            List<DatabaseStructInfoVo> databaseStructInfoVos = BeanUtil.copyToList(databaseStructInfoDtos, DatabaseStructInfoVo.class);
            return Rest.success(databaseStructInfoVos);
        } catch (Exception e) {
            log.error("Update database connection error,id is {}", id, e);
        }
        return Rest.error("error");
    }

    @PostMapping("/neo4j/retrieve/table")
    public Rest<?> retrieveNeo4jTable(@RequestBody SqlLineageTableNodeParamsDto sqlLineageTableNodeParamsDto) {
        try {
            validateNeo4jTableParams(sqlLineageTableNodeParamsDto);
            List<SqlLineageTableNodeDto> sqlLineageTableNodeDto = lineageService.retrieveNeo4jTableInfo(sqlLineageTableNodeParamsDto);
            List<SqlLineageTableNodeVo> sqlLineageTableNodeVos = BeanUtil.copyToList(sqlLineageTableNodeDto, SqlLineageTableNodeVo.class);
            return Rest.success(sqlLineageTableNodeVos);
        } catch (BusinessException be) {
            log.error("RetrieveNeo4jTable and validateNeo4jTableParams error, params are {}", JSONUtil.toJsonStr(sqlLineageTableNodeParamsDto), be);
            return Rest.error(be.getMessage());
        } catch (Exception e) {
            log.error("RetrieveNeo4jTable  error, params are {}", JSONUtil.toJsonStr(sqlLineageTableNodeParamsDto), e);
            return Rest.error("未知异常");
        }
    }

    private void validateNeo4jTableParams(SqlLineageTableNodeParamsDto sqlLineageTableNodeParamsDto) {
        if (StrUtil.isEmpty(sqlLineageTableNodeParamsDto.getId())) {
            throw new BusinessException("连接信息ID不能为空！");
        }
        if (StrUtil.isEmpty(sqlLineageTableNodeParamsDto.getDatabaseName())) {
            throw new BusinessException("数据库不能为空！");
        }
    }

    @Autowired
    public void setLineageService(LineageService lineageService) {
        this.lineageService = lineageService;
    }
}
