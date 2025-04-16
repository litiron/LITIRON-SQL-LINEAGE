package com.litiron.code.lineage.sql.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litiron.code.lineage.sql.common.BusinessException;
import com.litiron.code.lineage.sql.common.Rest;
import com.litiron.code.lineage.sql.constants.ExceptionCategoryConstant;
import com.litiron.code.lineage.sql.dto.database.*;
import com.litiron.code.lineage.sql.service.DatabaseComplexService;
import com.litiron.code.lineage.sql.vo.database.DatabaseConnectionVo;
import com.litiron.code.lineage.sql.vo.database.DatabaseStructInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @description: 数据库相关控制层
 * @author: 李日红
 * @create: 2024/11/26 22:01
 */
@RequestMapping("/database")
@RestController
@Slf4j
public class DatabaseController {
    private DatabaseComplexService databaseComplexService;

    @GetMapping("/retrieve/connection")
    public Rest<List<DatabaseConnectionVo>> retrieveDatabaseConnectionInfo() {
        List<DatabaseConnectionDto> connectionDtoList = databaseComplexService.retrieveDatabaseConnectionInfo();
        List<DatabaseConnectionVo> connectionVos = BeanUtil.copyToList(connectionDtoList, DatabaseConnectionVo.class);
        return Rest.success(connectionVos);
    }

    @PostMapping("/myConnection/retrieve")
    public Rest<?> retrieveMyDatabaseConnectionInfo(@RequestBody QueryDatabaseConnectionParamsDto queryDatabaseConnectionParamsDto) {
        try {
            validateDbConnectionDto(queryDatabaseConnectionParamsDto);
            IPage<DatabaseConnectionDto> dtoPage = databaseComplexService.retrieveMyDatabaseConnectionInfo(queryDatabaseConnectionParamsDto);
            return Rest.success(dtoPage.getRecords(), dtoPage.getTotal());

        } catch (BusinessException be) {
            log.error("RetrieveMyDatabaseConnectionInfo business error ,and params are {}", JSONUtil.toJsonStr(queryDatabaseConnectionParamsDto), be);
            return Rest.error(be.getMessage());
        } catch (Exception e) {
            log.error("RetrieveMyDatabaseConnectionInfo error ,and params are {}", JSONUtil.toJsonStr(queryDatabaseConnectionParamsDto), e);
            return Rest.error(ExceptionCategoryConstant.UNKNOWN_EXCEPTION);
        }
    }

    @PostMapping("/myConnection/add")
    public Rest<?> addDatabaseConnectionInfo(@RequestBody DatabaseConnectionParamsDto databaseConnectionParamsDto) {
        try {
            validateAddDbConnectionDto(databaseConnectionParamsDto);
            databaseComplexService.addDatabaseConnectionInfo(databaseConnectionParamsDto);
            return Rest.success("添加成功");
        } catch (BusinessException be) {
            log.error("AddDatabaseConnectionInfo business error ,and params are {}", JSONUtil.toJsonStr(databaseConnectionParamsDto), be);
            return Rest.error(be.getMessage());
        } catch (Exception e) {
            log.error("AddDatabaseConnectionInfo error ,and params are {}", JSONUtil.toJsonStr(databaseConnectionParamsDto), e);
            return Rest.error(ExceptionCategoryConstant.UNKNOWN_EXCEPTION);
        }
    }

    @GetMapping("/myConnection/delete")
    public Rest<?> deleteDatabaseConnectionInfo(@RequestParam("id") String id) {
        try {
            databaseComplexService.deleteDatabaseConnectionInfo(id);
            return Rest.success("删除成功");
        } catch (BusinessException be) {
            log.error("DeleteDatabaseConnectionInfo business error ,and id is {}", id, be);
            return Rest.error(be.getMessage());
        } catch (Exception e) {
            log.error("DeleteDatabaseConnectionInfo error ,and id is {}", id, e);
            return Rest.error(ExceptionCategoryConstant.UNKNOWN_EXCEPTION);
        }
    }

    @PostMapping("/myConnection/edit")
    public Rest<?> editDatabaseConnectionInfo(@RequestBody DatabaseConnectionParamsDto databaseConnectionParamsDto) {
        try {
            validateEditDbConnectionDto(databaseConnectionParamsDto);
            databaseComplexService.editDatabaseConnectionInfo(databaseConnectionParamsDto);
            return Rest.success("编辑成功");
        } catch (BusinessException be) {
            log.error("EditDatabaseConnectionInfo business error ,and params are {}", JSONUtil.toJsonStr(databaseConnectionParamsDto), be);
            return Rest.error(be.getMessage());
        } catch (Exception e) {
            log.error("EditDatabaseConnectionInfo error ,and params are {}", JSONUtil.toJsonStr(databaseConnectionParamsDto), e);
            return Rest.error(ExceptionCategoryConstant.UNKNOWN_EXCEPTION);
        }
    }

    @GetMapping("/retrieve/pgDbs")
    public Rest<List<String>> retrievePgDatabasesInfo(@RequestParam(value = "id") String id) {
        List<String> pgDbList = databaseComplexService.retrievePgDatabasesInfo(id);
        return Rest.success(pgDbList);
    }

    @GetMapping("/connection/update")
    public Rest<?> updateDatabaseConnection(@RequestParam(value = "id") String id, @RequestParam(value = "pgDbName", defaultValue = "", required = false) String pgDbName) {
        try {
            List<DatabaseStructInfoDto> databaseStructInfoDtoList = databaseComplexService.updateDatabaseConnection(id, pgDbName);
            List<DatabaseStructInfoVo> databaseStructInfoVos = BeanUtil.copyToList(databaseStructInfoDtoList, DatabaseStructInfoVo.class);
            return Rest.success(databaseStructInfoVos);
        } catch (Exception e) {
            log.error("Update database connection error,id is {}", id, e);
        }
        return Rest.error("error");
    }

    @PostMapping("/retrieve/table/details")
    public Rest<?> getTableDetails(@RequestBody TableDetailsParamsDto tableDetailsParamsDto) {
        IPage<Map<String, Object>> tableDetails = databaseComplexService.retrieveTableDetailsByPage(tableDetailsParamsDto);

        return Rest.success(tableDetails.getRecords(), tableDetails.getTotal());
    }

    private void validateEditDbConnectionDto(DatabaseConnectionParamsDto databaseConnectionParamsDto) {
        if (StrUtil.isEmpty(databaseConnectionParamsDto.getId())) {
            throw new BusinessException("id不能为空");
        }
    }

    private void validateAddDbConnectionDto(DatabaseConnectionParamsDto databaseConnectionParamsDto) {
        if (StrUtil.isEmpty(databaseConnectionParamsDto.getConnectionName())) {
            throw new BusinessException("连接名不能为空");
        }
        if (StrUtil.isEmpty(databaseConnectionParamsDto.getIp())) {
            throw new BusinessException("ip不能为空");
        }
        if (ObjectUtil.isEmpty(databaseConnectionParamsDto.getPort())) {
            throw new BusinessException("端口号不能为空");
        }
        if (StrUtil.isEmpty(databaseConnectionParamsDto.getType())) {
            throw new BusinessException("数据库类型不能为空");
        }
        if (StrUtil.isEmpty(databaseConnectionParamsDto.getUserName())) {
            throw new BusinessException("用户名不能为空");
        }
        if (StrUtil.isEmpty(databaseConnectionParamsDto.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
    }

    private void validateDbConnectionDto(QueryDatabaseConnectionParamsDto queryDatabaseConnectionParamsDto) {
        if (ObjectUtil.isEmpty(queryDatabaseConnectionParamsDto.getPageSize())) {
            throw new BusinessException("分页大小不能为空");
        }
        if (ObjectUtil.isEmpty(queryDatabaseConnectionParamsDto.getPageNumber())) {
            throw new BusinessException("分页数不能为空");
        }
    }

    @Autowired
    public void setDatabaseService(DatabaseComplexService databaseComplexService) {
        this.databaseComplexService = databaseComplexService;
    }

}
