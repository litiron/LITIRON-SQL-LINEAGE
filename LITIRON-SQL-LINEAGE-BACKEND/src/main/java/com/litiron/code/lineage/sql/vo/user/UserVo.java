package com.litiron.code.lineage.sql.vo.user;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 李日红
 * @description: 用户信息Vo
 * @create 2025/4/18 15:20
 */
@Setter
@Getter
public class UserVo {
    private String id;

    private String userName;

    private String password;

    private String nickName;

    private String email;

    private String address;

    private String phone;
}
