package com.litiron.code.lineage.sql.interceptors;

import cn.hutool.core.util.StrUtil;
import com.litiron.code.lineage.sql.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

import static com.litiron.code.lineage.sql.constants.RedisConstant.LOGIN_USER_KEY;
import static com.litiron.code.lineage.sql.constants.RedisConstant.LOGIN_USER_TTL;

/**
 * @author 李日红
 * @description: 登录拦截器
 * @create 2025/3/27 20:27
 */
@AllArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Token");

        if (StrUtil.isBlank(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        String tokenKey = LOGIN_USER_KEY + token;
        String uid = stringRedisTemplate.opsForValue().get(tokenKey);
        if (StrUtil.isEmpty(uid)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        ThreadLocalUtil.setUser(uid);
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 移除用户数据，防止内存泄漏
        ThreadLocalUtil.remove();
    }
}
