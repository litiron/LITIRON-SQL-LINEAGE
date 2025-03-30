package com.litiron.code.lineage.sql.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 李日红
 * @description: 数据库连接信息
 * @create 2024/12/1 14:06
 */
@Getter
@Setter
@TableName(value = "user")
public class UserEntity {
    private String id;

    private String userName;

    private String password;

    private String nickName;

    private String avatar;

    private String email;

    private String address;

    private Integer phone;

    private String host;
}
