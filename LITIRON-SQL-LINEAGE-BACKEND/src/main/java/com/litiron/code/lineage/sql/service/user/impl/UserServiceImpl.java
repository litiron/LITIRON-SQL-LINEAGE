package com.litiron.code.lineage.sql.service.user.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.litiron.code.lineage.sql.common.BusinessException;
import com.litiron.code.lineage.sql.dao.UserRepository;
import com.litiron.code.lineage.sql.dto.user.UserParamsDto;
import com.litiron.code.lineage.sql.entity.user.UserEntity;
import com.litiron.code.lineage.sql.service.user.UserService;
import com.litiron.code.lineage.sql.utils.Md5Util;
import com.litiron.code.lineage.sql.utils.ThreadLocalUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.litiron.code.lineage.sql.constants.RedisConstant.LOGIN_USER_KEY;
import static com.litiron.code.lineage.sql.constants.RedisConstant.LOGIN_USER_TTL;

/**
 * @author 李日红
 * @description: 用户服务实现类
 * @create 2025/3/27 19:25
 */
@AllArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final StringRedisTemplate stringRedisTemplate;
    @Override
    public void register(UserParamsDto userParamsDto) {
        UserEntity user = getUser(userParamsDto.getUserName());
        if (ObjectUtil.isNotEmpty(user)) {
            throw new BusinessException("该用户名已存在");
        }
        UserEntity newUser = buildUserInfo(userParamsDto);
        userRepository.insert(newUser);
    }

    @Override
    public String login(UserParamsDto userParamsDto) {
        UserEntity user = getUser(userParamsDto.getUserName());
        if (ObjectUtil.isEmpty(user)) {
            throw new BusinessException("该用户名不存在");
        }
        if (!Md5Util.getMD5String(userParamsDto.getPassword()).equals(user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        String tokenKey = LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForValue().set(tokenKey, user.getId(), LOGIN_USER_TTL, TimeUnit.MINUTES);
        return token;
    }

    private UserEntity buildUserInfo(UserParamsDto userParamsDto) {
        UserEntity newUser = new UserEntity();
        String safe_pwd = Md5Util.getMD5String(userParamsDto.getPassword());
        newUser.setUserName(userParamsDto.getUserName());
        newUser.setNickName(userParamsDto.getUserName());
        newUser.setPassword(safe_pwd);
        return newUser;
    }

    private UserEntity getUser(String userName) {
        return userRepository.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUserName, userName));
    }
}
