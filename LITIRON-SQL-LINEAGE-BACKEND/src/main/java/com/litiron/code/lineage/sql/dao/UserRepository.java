package com.litiron.code.lineage.sql.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litiron.code.lineage.sql.entity.user.UserEntity;
import org.springframework.stereotype.Repository;

/**
 * @description: 用户信息dao层定义
 * @author: 李日红
 * @create: 2025/3/27 20:01
 */
@Repository
public interface UserRepository extends BaseMapper<UserEntity> {
    UserEntity getUser(String userName);
}
