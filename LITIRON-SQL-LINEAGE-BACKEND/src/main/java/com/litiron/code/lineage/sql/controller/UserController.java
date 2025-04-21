package com.litiron.code.lineage.sql.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.litiron.code.lineage.sql.common.BusinessException;
import com.litiron.code.lineage.sql.common.Rest;
import com.litiron.code.lineage.sql.constants.ExceptionCategoryConstant;
import com.litiron.code.lineage.sql.dto.user.UserDto;
import com.litiron.code.lineage.sql.dto.user.UserInfoUpdateParamsDto;
import com.litiron.code.lineage.sql.dto.user.UserParamsDto;
import com.litiron.code.lineage.sql.service.user.UserService;
import com.litiron.code.lineage.sql.vo.user.UserVo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/info")
    public Rest<?> getUserInfo() {
        try {
            UserDto userDto = userService.getUserInfo();
            return Rest.success(BeanUtil.copyProperties(userDto, UserVo.class));
        } catch (BusinessException be) {
            log.error("GetUserInfo error ", be);
            return Rest.error(be.getMessage());
        } catch (Exception e) {
            log.error("GetUserInfo error ", e);
            return Rest.error(ExceptionCategoryConstant.UNKNOWN_EXCEPTION);
        }
    }

    @PostMapping("/updateInfo")
    public Rest<?> updateInfo(@RequestBody UserInfoUpdateParamsDto infoUpdateParamsDto) {
        try {
            userService.updateInfo(infoUpdateParamsDto);
            return Rest.success("更新信息成功");
        } catch (BusinessException be) {
            log.error("UpdateInfo error and params are {}", JSONUtil.toJsonStr(infoUpdateParamsDto), be);
            return Rest.error(be.getMessage());
        } catch (Exception e) {
            log.error("UpdateInfo error and params are {}", JSONUtil.toJsonStr(infoUpdateParamsDto), e);
            return Rest.error(ExceptionCategoryConstant.UNKNOWN_EXCEPTION);
        }
    }

    @PostMapping("/updatePassword")
    public Rest<?> updatePassword(@RequestBody UserInfoUpdateParamsDto infoUpdateParamsDto) {
        try {
            validatePasswordParams(infoUpdateParamsDto);
            userService.updatePassword(infoUpdateParamsDto);
            return Rest.success("更新密码成功");
        } catch (BusinessException be) {
            log.error("UpdatePassword error and params are {}", JSONUtil.toJsonStr(infoUpdateParamsDto), be);
            return Rest.error(be.getMessage());
        } catch (Exception e) {
            log.error("UpdatePassword error and params are {}", JSONUtil.toJsonStr(infoUpdateParamsDto), e);
            return Rest.error(ExceptionCategoryConstant.UNKNOWN_EXCEPTION);
        }
    }

    private void validatePasswordParams(UserInfoUpdateParamsDto infoUpdateParamsDto) {
        if (StrUtil.isEmpty(infoUpdateParamsDto.getNewPassword())) {
            throw new BusinessException("新密码不能为空");
        }
        if (StrUtil.isEmpty(infoUpdateParamsDto.getOldPassword())) {
            throw new BusinessException("旧密码不能为空");
        }
    }

    private void validateUserParams(UserParamsDto userParamsDto) {
        if (StrUtil.isEmpty(userParamsDto.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        if (StrUtil.isEmpty(userParamsDto.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
    }
}
