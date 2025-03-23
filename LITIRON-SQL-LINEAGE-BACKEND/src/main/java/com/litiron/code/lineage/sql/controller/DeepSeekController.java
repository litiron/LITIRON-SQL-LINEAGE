package com.litiron.code.lineage.sql.controller;

import com.litiron.code.lineage.sql.dto.deepseek.DeepseekRequestDto;
import com.litiron.code.lineage.sql.service.deepseek.DeepseekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author 李日红
 * @description: deepseek控制层
 * @create 2025/3/7 15:30
 */
@RestController
@RequestMapping("/deepseek")
public class DeepSeekController {
    private DeepseekService deepseekService;

    @GetMapping(path = "/ask", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter askDeepSeek(@RequestParam(value = "content") String content) {
        try {
            return deepseekService.sendStreamRequest(DeepseekRequestDto.create(content));
        } catch (IOException e) {
            throw new RuntimeException("API调用失败", e);
        }
    }
    @Autowired
    public void setDeepSeekService(DeepseekService deepSeekService) {
        this.deepseekService = deepSeekService;
    }

}
