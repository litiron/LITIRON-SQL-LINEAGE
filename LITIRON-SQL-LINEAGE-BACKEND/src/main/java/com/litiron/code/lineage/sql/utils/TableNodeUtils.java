package com.litiron.code.lineage.sql.utils;

/**
 * @description: table node节点相关工具包
 * @author: Litiron
 * @create: 2025-03-08 17:00
 **/
public class TableNodeUtils {
    private static final String TABLE_KEY_PATTERN = "%s:%s";

    /**
     * 用于node连接过程中的key判断，保证两个node不会重复添加relationShip
     *
     * @param t1 表名1
     * @param t2 表名2
     * @return 自然排序后的表名
     */
    public static String generateKey(String t1, String t2) {
        return t1.compareTo(t2) >= 0 ? String.format(TABLE_KEY_PATTERN, t1, t2) : String.format(TABLE_KEY_PATTERN, t2, t1);
    }
}
