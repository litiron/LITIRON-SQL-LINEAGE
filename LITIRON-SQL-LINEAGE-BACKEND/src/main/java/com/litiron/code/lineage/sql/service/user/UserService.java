package com.litiron.code.lineage.sql.service.user;

import com.litiron.code.lineage.sql.dto.user.UserParamsDto;

/**
 * @description: 用户操作相关Service
 * @author: 李日红
 * @create: 2025/3/27 19:25
 */
public interface UserService {
    /**
     * @description: 用户注册
     * @param: userParamsDto  注册参数信息
     * @return: void
     * @author: 李日红
     * @create: 2025/3/27 19:45
     */
    void register(UserParamsDto userParamsDto);

    /**
     * @description: 用户登录
     * @param: userParamsDto  登录参数信息
     * @return: void
     * @author: 李日红
     * @create: 2025/3/27 20:10
     */
    String login(UserParamsDto userParamsDto);
}
