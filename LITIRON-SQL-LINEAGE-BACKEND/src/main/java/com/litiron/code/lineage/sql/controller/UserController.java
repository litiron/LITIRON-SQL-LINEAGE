package com.litiron.code.lineage.sql.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.litiron.code.lineage.sql.common.BusinessException;
import com.litiron.code.lineage.sql.common.Rest;
import com.litiron.code.lineage.sql.constants.ExceptionCategoryConstant;
import com.litiron.code.lineage.sql.dto.user.UserParamsDto;
import com.litiron.code.lineage.sql.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李日红
 * @description: 用户相关控制器
 * @create 2025/3/27 19:19
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public Rest<?> register(@RequestBody UserParamsDto userParamsDto) {
        try {
            validateUserParams(userParamsDto);
            userService.register(userParamsDto);
        } catch (BusinessException be) {
            log.error("Register error and params are {}", JSONUtil.toJsonStr(userParamsDto), be);
            return Rest.error(be.getMessage());
        } catch (Exception e) {
            log.error("Register error and params are {}", JSONUtil.toJsonStr(userParamsDto), e);
            return Rest.error(ExceptionCategoryConstant.UNKNOWN_EXCEPTION);
        }
        return Rest.success("注册成功");
    }

    @PostMapping("/login")
    public Rest<?> login(@RequestBody UserParamsDto userParamsDto) {
        try {
            validateUserParams(userParamsDto);
            return Rest.success(userService.login(userParamsDto));
        } catch (BusinessException be) {
            log.error("Login error and params are {}", JSONUtil.toJsonStr(userParamsDto), be);
            return Rest.error(be.getMessage());
        } catch (Exception e) {
            log.error("Login error and params are {}", JSONUtil.toJsonStr(userParamsDto), e);
            return Rest.error(ExceptionCategoryConstant.UNKNOWN_EXCEPTION);
        }
    }

    private void validateUserParams(UserParamsDto userParamsDto) {
        if (StrUtil.isEmpty(userParamsDto.getUserName())) {
            throw new BusinessException("用户名不能为空");
        }
        if (StrUtil.isEmpty(userParamsDto.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
    }
}
