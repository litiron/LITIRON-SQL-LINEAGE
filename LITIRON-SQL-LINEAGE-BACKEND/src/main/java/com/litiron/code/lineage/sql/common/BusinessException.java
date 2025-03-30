package com.litiron.code.lineage.sql.common;

/**
 * @author 李日红
 * @description:
 * @create 2025/2/22 17:06
 */
public class BusinessException extends RuntimeException {

    String message;

    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
    }


}
