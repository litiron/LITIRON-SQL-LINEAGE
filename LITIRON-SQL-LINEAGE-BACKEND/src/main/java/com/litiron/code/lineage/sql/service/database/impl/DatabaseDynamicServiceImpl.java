package com.litiron.code.lineage.sql.service.database.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.litiron.code.lineage.sql.dao.DatabaseDynamicRepository;
import com.litiron.code.lineage.sql.service.database.DatabaseDynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author 李日红
 * @description: 动态数据库service实现类
 * @create 2025/2/8 17:29
 */
@Service
public class DatabaseDynamicServiceImpl implements DatabaseDynamicService {
    private DatabaseDynamicRepository databaseDynamicRepository;

    @Override
    public IPage<Map<String, Object>> retrieveTableDetails(IPage<Map<String, Object>> page, String tableName) {
        return databaseDynamicRepository.retrieveTableDetails(page, tableName);
    }

    @Autowired
    public void setDatabaseDynamicRepository(DatabaseDynamicRepository databaseDynamicRepository) {
        this.databaseDynamicRepository = databaseDynamicRepository;
    }

}
