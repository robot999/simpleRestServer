package com.jiajin.simplerestserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author ljj
 * @create 2026 01 09 20:07
 */
@Configuration
public class WebClientConfig {

    @Bean
    WebClient githubWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .build();
    }
}
