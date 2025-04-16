package com.litiron.code.lineage.sql.config;

import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * @author 李日红
 * @description: webflux 配置
 * @create 2025/3/15 18:26
 */
@Configuration
public class WebFluxClientConfig {

    @Value("${langchain4j.openai.chat-model.base-url}")
    private String apiUrl;

    @Value("${langchain4j.openai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.openai.chat-model.model-name}")
    private String modelName;

    @Bean(value = "deepSeekWebClient")
    public WebClient deepSeekWebClient() {
        return WebClient
                .builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean(value = "Langchain4jWebClient")
    public OpenAiStreamingChatModel Langchain4jWebClient() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .baseUrl(apiUrl)
                .temperature(0.8)
                .maxTokens(4096)
                .timeout(Duration.ofSeconds(600))
                .build();

    }

    @Bean(value = "embeddingModel")
    public OpenAiEmbeddingModel openAiEmbeddingModel() {
        return OpenAiEmbeddingModel
                .builder()
                .baseUrl("https://api.chatanywhere.tech")
                .apiKey("sk-BcMZZa1n93sxzjNnth3t3dw6s2bnZlnMvjhefBaTiLfhwL62")
                .modelName("text-embedding-3-small")
                .build();
    }
}
