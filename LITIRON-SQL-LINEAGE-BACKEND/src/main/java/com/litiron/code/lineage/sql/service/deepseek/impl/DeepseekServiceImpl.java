package com.litiron.code.lineage.sql.service.deepseek.impl;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.litiron.code.lineage.sql.dto.deepseek.DeepseekRequestDto;
import com.litiron.code.lineage.sql.dto.deepseek.DeepseekResponseDto;
import com.litiron.code.lineage.sql.service.deepseek.DeepseekService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author 李日红
 * @description: deepseek服务实现类
 * @create 2025/3/7 14:30
 */
@Service
@Slf4j
public class DeepseekServiceImpl implements DeepseekService {
    private OkHttpClient okHttpClient;
    private WebClient deepSeekWebClient;
    private ObjectMapper objectMapper = new ObjectMapper();
    @Value("${deepseek.api.url}")
    private String apiUrl;
    @Value("${deepseek.api.key}")
    private String apiKey;

    public SseEmitter sendStreamRequest(DeepseekRequestDto request) {
        SseEmitter emitter = new SseEmitter();
        StringBuilder markdownBuffer = new StringBuilder();
        deepSeekWebClient.post().bodyValue(request).accept(MediaType.TEXT_EVENT_STREAM).retrieve().bodyToFlux(String.class).doOnError(e -> log.error("Exception ", e)).doOnNext(res -> {
            if ("[DONE]".equals(res)) {
                emitter.complete();
                return;
            }
            DeepseekResponseDto data = JSONUtil.toBean(res, DeepseekResponseDto.class);
            markdownBuffer.append(data.getChoices().get(0).getDelta().getContent());
            // 查找自然分段点（代码块结束/空行/标点）
            int splitPos = findNaturalSplit(markdownBuffer);
            if (splitPos > 0) {
                String chunk = markdownBuffer.substring(0, splitPos);
                try {
                    emitter.send(SseEmitter.event().data(chunk));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                markdownBuffer.delete(0, splitPos);
            }
        }).subscribe();
        return emitter;
    }

    private int findNaturalSplit(StringBuilder buffer) {
        int codeBlockEnd = buffer.lastIndexOf("```\n");
        if (codeBlockEnd != -1) {
            return codeBlockEnd + 4;
        }
        int doubleNewline = buffer.lastIndexOf("\n\n");
        if (doubleNewline != -1) {
            return doubleNewline + 2;
        }
        int lastSentenceEnd = Math.max(buffer.lastIndexOf("。"), Math.max(buffer.lastIndexOf("！"), Math.max(buffer.lastIndexOf("？"), buffer.lastIndexOf("\n"))));
        return lastSentenceEnd != -1 ? lastSentenceEnd + 1 : -1;
    }

    @Autowired
    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setDeepSeekWebClient(WebClient deepSeekWebClient) {
        this.deepSeekWebClient = deepSeekWebClient;
    }
}
