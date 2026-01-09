package com.jiajin.simplerestserver.service;

import com.jiajin.simplerestserver.controller.dto.GithubApiResponseDTO;
import com.jiajin.simplerestserver.exception.GithubRateLimitException;
import com.jiajin.simplerestserver.exception.RepositoryNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author ljj
 * @create 2026 01 09 20:05
 */
@Component
public class GithubApiClient {

    private final WebClient webClient;

    public GithubApiClient(WebClient webClient) {
        this.webClient = webClient;
    }


    public GithubApiResponseDTO fetchRepository(String owner, String repo) {
        /**
         * 403 -> GitHub will return Header like this (but use 429 prompt friendly )：
         * X-RateLimit-Remaining
         * X-RateLimit-Reset
         */
        return webClient.get()
                .uri("/repos/{owner}/{repo}", owner, repo)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.error(
                                new RepositoryNotFoundException(owner, repo)
                        ))
                .onStatus(  status -> status.value() == 403,
                        response -> response.headers()
                                .asHttpHeaders()
                                .getFirst("X-RateLimit-Reset") != null
                                ? Mono.error(new GithubRateLimitException(
                                "Rate limit exceeded. Reset at " +
                                        response.headers()
                                                .asHttpHeaders()
                                                .getFirst("X-RateLimit-Reset")
                        ))
                                : Mono.error(new GithubRateLimitException()))
                .bodyToMono(GithubApiResponseDTO.class)
                .block();
    }
}
