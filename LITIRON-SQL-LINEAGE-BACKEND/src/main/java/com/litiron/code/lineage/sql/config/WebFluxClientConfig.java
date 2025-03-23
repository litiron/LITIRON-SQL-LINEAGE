package com.litiron.code.lineage.sql.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author 李日红
 * @description: webflux 配置
 * @create 2025/3/15 18:26
 */
@Configuration
public class WebFluxClientConfig {

    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Bean(value = "deepSeekWebClient")
    public WebClient deepSeekWebClient() {
        return WebClient
                .builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization","Bearer " + apiKey )
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
