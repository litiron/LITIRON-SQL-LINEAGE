package com.litiron.code.lineage.sql.dto.user;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 李日红
 * @description: 用户信息更新参数DTO
 * @create 2025/3/27 19:22
 */
@Setter
@Getter
public class UserInfoUpdateParamsDto {

    private String nickName;

    private String phone;

    private String email;

    private String address;

    private String newPassword;

    private String oldPassword;
}
