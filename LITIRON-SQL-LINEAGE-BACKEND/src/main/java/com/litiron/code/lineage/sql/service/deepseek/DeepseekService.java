package com.litiron.code.lineage.sql.service.deepseek;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.litiron.code.lineage.sql.dto.deepseek.DeepseekRequestDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author 李日红
 * @description: deepseek服务类
 * @create 2025/3/7 14:30
 */
public interface DeepseekService {
    SseEmitter sendStreamRequest(DeepseekRequestDto deepseekRequestDto) throws JsonProcessingException;
}
