package com.litiron.code.lineage.sql.service.database.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.litiron.code.lineage.sql.dao.DatabaseConnectionRepository;
import com.litiron.code.lineage.sql.entity.database.DatabaseConnectionEntity;
import com.litiron.code.lineage.sql.service.database.DatabaseConnectionService;
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
    public List<DatabaseConnectionEntity> getaAllDatabaseConnectionInfo() {
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
        return databaseConnectionRepository.selectList(new LambdaQueryWrapper<DatabaseConnectionEntity>()
                        .select(DatabaseConnectionEntity::getType).groupBy(DatabaseConnectionEntity::getType))
                .stream().map(DatabaseConnectionEntity::getType).collect(Collectors.toList());
    }

    @Autowired
    public void setDatabaseConnectionRepository(DatabaseConnectionRepository databaseConnectionRepository) {
        this.databaseConnectionRepository = databaseConnectionRepository;
    }

}
