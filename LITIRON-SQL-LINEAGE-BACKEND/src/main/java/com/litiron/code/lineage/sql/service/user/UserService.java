package com.litiron.code.lineage.sql.service.user;

import com.litiron.code.lineage.sql.dto.user.UserDto;
import com.litiron.code.lineage.sql.dto.user.UserInfoUpdateParamsDto;
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

    /**
     * @description: 获取用户信息
     * @return: com.litiron.code.lineage.sql.dto.user.UserDto
     * @author: 李日红
     * @create: 2025/4/18 15:23
     */
    UserDto getUserInfo();

    /**
     * @description: 用户更新个人信息
     * @param: infoUpdateParamsDto 更新信息参数
     * @return: void
     * @author: 李日红
     * @create: 2025/4/18 15:57
     */
    void updateInfo(UserInfoUpdateParamsDto infoUpdateParamsDto);

    /**
     * @description: 修改密码
     * @param: infoUpdateParamsDto 密码参数 dto
     * @return: void
     * @author: 李日红
     * @create: 2025/4/18 16:38
     */
    void updatePassword(UserInfoUpdateParamsDto infoUpdateParamsDto);
}
