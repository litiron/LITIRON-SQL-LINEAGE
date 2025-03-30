package com.litiron.code.lineage.sql.utils;

import java.util.Map;

/**
 * @author 李日红
 * @description: 线程工具类
 * @create 2025/3/27 20:34
 */
public class ThreadLocalUtil {
    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();

    public static void setUser(String uid) {
        THREAD_LOCAL.set(uid);
    }

    public static String getUser() {
        return THREAD_LOCAL.get();
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

}
