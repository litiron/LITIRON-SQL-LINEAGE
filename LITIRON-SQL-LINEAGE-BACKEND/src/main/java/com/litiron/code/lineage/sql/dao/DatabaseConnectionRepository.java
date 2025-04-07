package com.litiron.code.lineage.sql.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litiron.code.lineage.sql.entity.database.DatabaseConnectionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description: 数据库连接信息dao层定义
 * @author: 李日红
 * @create: 2024/12/1 14:11
 */
@Mapper
public interface DatabaseConnectionRepository extends BaseMapper<DatabaseConnectionEntity> {

}
