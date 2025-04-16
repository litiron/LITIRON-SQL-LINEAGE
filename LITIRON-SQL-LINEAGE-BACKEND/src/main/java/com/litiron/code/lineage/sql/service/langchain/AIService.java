package com.litiron.code.lineage.sql.service.langchain;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @description: AI服务类
 * @author: 李日红
 * @create: 2025/4/16 19:08
 */
public interface AIService {
    /**
     * @description: 提问
     * @param: question  问题
     * @return: org.springframework.web.servlet.mvc.method.annotation.SseEmitter
     * @author: 李日红
     * @create: 2025/4/16 19:08
     */
    SseEmitter askQuestion(String question);

    /**
     * @description: 存储表的结构于向量数据库
     * @return: void
     * @author: 李日红
     * @create: 2025/4/16 19:08
     */
    void storageVector();
}
