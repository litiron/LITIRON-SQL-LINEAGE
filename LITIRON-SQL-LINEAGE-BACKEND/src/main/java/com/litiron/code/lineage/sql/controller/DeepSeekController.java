package com.litiron.code.lineage.sql.controller;

import com.litiron.code.lineage.sql.common.Rest;
import com.litiron.code.lineage.sql.dto.deepseek.DeepseekRequestDto;
import com.litiron.code.lineage.sql.service.deepseek.DeepseekService;
import com.litiron.code.lineage.sql.service.langchain.AIService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author 李日红
 * @description: deepseek控制层
 * @create 2025/3/7 15:30
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/deepseek")
public class DeepSeekController {
    private final DeepseekService deepseekService;
    private final AIService aiService;

    @GetMapping(path = "/ask", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter askDeepSeek(@RequestParam(value = "content") String content) {
        try {
            return deepseekService.sendStreamRequest(DeepseekRequestDto.create(content));
        } catch (IOException e) {
            throw new RuntimeException("API调用失败", e);
        }
    }

    @GetMapping(path = "/askLangchain", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter ask(@RequestParam(value = "content") String question) {
        try {
            return aiService.askQuestion(question);
        } catch (Exception e) {
            throw new RuntimeException("API调用失败", e);
        }
    }

    @PostMapping("/storagePgVector")
    public Rest<?> storagePgVector() {
        try {
            aiService.storageVector();
            return Rest.success("存储表结构信息于向量数据库成功");
        } catch (Exception e) {
            log.error("StoragePgVector error", e);
            return Rest.error("存储表结构信息于向量数据库失败");
        }
    }
}
