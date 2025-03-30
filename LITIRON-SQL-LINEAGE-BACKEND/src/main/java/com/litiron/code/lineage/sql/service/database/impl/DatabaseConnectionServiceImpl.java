package com.litiron.code.lineage.sql.service.database.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litiron.code.lineage.sql.common.BusinessException;
import com.litiron.code.lineage.sql.dao.DatabaseConnectionRepository;
import com.litiron.code.lineage.sql.dto.database.DatabaseConnectionDto;
import com.litiron.code.lineage.sql.dto.database.QueryDatabaseConnectionParamsDto;
import com.litiron.code.lineage.sql.entity.database.DatabaseConnectionEntity;
import com.litiron.code.lineage.sql.service.database.DatabaseConnectionService;
import com.litiron.code.lineage.sql.utils.Md5Util;
import com.litiron.code.lineage.sql.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 李日红
 * @description: 数据库连接Service实现类
 * @create 2024/12/1 18:06
 */
@Service
public class DatabaseConnectionServiceImpl implements DatabaseConnectionService {
    private DatabaseConnectionRepository databaseConnectionRepository;

    @Override
    public List<DatabaseConnectionEntity> getAllDatabaseConnectionInfo() {
        return databaseConnectionRepository.selectList(new QueryWrapper<>());
    }

    @Override
    public DatabaseConnectionEntity getDatabaseConnectionInfoById(String id) {
        return databaseConnectionRepository.selectById(id);
    }

    @Override
    public List<DatabaseConnectionEntity> getDatabaseConnectionInfoByType(String type) {
        return databaseConnectionRepository.selectList(new LambdaQueryWrapper<DatabaseConnectionEntity>().eq(DatabaseConnectionEntity::getType, type));
    }

    @Override
    public List<String> getAllDatabaseType() {
        return databaseConnectionRepository.selectList(new LambdaQueryWrapper<DatabaseConnectionEntity>().select(DatabaseConnectionEntity::getType).groupBy(DatabaseConnectionEntity::getType)).stream().map(DatabaseConnectionEntity::getType).collect(Collectors.toList());
    }

    @Override
    public IPage<DatabaseConnectionDto> getDatabaseConnectionPageByUid(QueryDatabaseConnectionParamsDto queryDatabaseConnectionParamsDto) {
        IPage<DatabaseConnectionEntity> page = new Page<>(queryDatabaseConnectionParamsDto.getPageNumber(), queryDatabaseConnectionParamsDto.getPageSize());
        LambdaQueryWrapper<DatabaseConnectionEntity> queryWrapper = buildQueryConnectionWrapper(queryDatabaseConnectionParamsDto);
        IPage<DatabaseConnectionEntity> entityPage = databaseConnectionRepository.selectPage(page, queryWrapper);
        return convert2DbConnectionDtoPage(entityPage);
    }

    @Override
    public void addDatabaseConnection(DatabaseConnectionEntity databaseConnectionEntity) {
        String uid = ThreadLocalUtil.getUser();
        databaseConnectionEntity.setUId(uid);
        databaseConnectionEntity.setPassword(Md5Util.getMD5String(databaseConnectionEntity.getPassword()));
        databaseConnectionRepository.insert(databaseConnectionEntity);
    }

    @Override
    public void deleteDatabaseConnection(String id) {
        DatabaseConnectionEntity databaseConnection = getDatabaseConnectionInfoById(id);
        if (ObjectUtil.isEmpty(databaseConnection)) {
            throw new BusinessException("该数据库连接信息id不存在");
        }
        databaseConnectionRepository.deleteById(id);
    }

    @Override
    public void editDatabaseConnection(DatabaseConnectionEntity databaseConnectionEntity) {
        DatabaseConnectionEntity databaseConnection = getDatabaseConnectionInfoById(databaseConnectionEntity.getId());
        if (ObjectUtil.isEmpty(databaseConnection)) {
            throw new BusinessException("该数据库连接信息id不存在");
        }
        databaseConnectionEntity.setPassword(Md5Util.getMD5String(databaseConnectionEntity.getPassword()));
        databaseConnectionRepository.updateById(databaseConnectionEntity);
    }

    private IPage<DatabaseConnectionDto> convert2DbConnectionDtoPage(IPage<DatabaseConnectionEntity> entityPage) {
        return entityPage.convert(entity -> BeanUtil.copyProperties(entity, DatabaseConnectionDto.class));
    }

    private LambdaQueryWrapper<DatabaseConnectionEntity> buildQueryConnectionWrapper(QueryDatabaseConnectionParamsDto queryDatabaseConnectionParamsDto) {
        String uid = ThreadLocalUtil.getUser();
        LambdaQueryWrapper<DatabaseConnectionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DatabaseConnectionEntity::getUId, uid);
        if (StrUtil.isNotEmpty(queryDatabaseConnectionParamsDto.getConnectionName())) {
            queryWrapper.like(DatabaseConnectionEntity::getConnectionName, queryDatabaseConnectionParamsDto.getConnectionName());
        }
        if (StrUtil.isNotEmpty(queryDatabaseConnectionParamsDto.getType())) {
            queryWrapper.like(DatabaseConnectionEntity::getType, queryDatabaseConnectionParamsDto.getType());
        }
        return queryWrapper;
    }


    @Autowired
    public void setDatabaseConnectionRepository(DatabaseConnectionRepository databaseConnectionRepository) {
        this.databaseConnectionRepository = databaseConnectionRepository;
    }

}
