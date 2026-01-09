package com.jiajin.simplerestserver.service;

import com.jiajin.simplerestserver.controller.dto.GithubApiResponseDTO;
import com.jiajin.simplerestserver.controller.dto.GithubRepositoryResponseDTO;
import com.jiajin.simplerestserver.infrastructure.dao.RepositoryCacheRepository;
import com.jiajin.simplerestserver.infrastructure.entity.RepositoryCacheEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * @author ljj
 * @create 2026 01 09 20:04
 */
@Service
@RequiredArgsConstructor
public class GithubRepositoryService {
    private final RepositoryCacheRepository cacheRepository;
    private final GithubApiClient githubApiClient;

    public GithubRepositoryResponseDTO getRepository(String owner, String repo) {
        return cacheRepository.findByOwnerAndName(owner, repo)
                .map(this::toResponse)
                .orElseGet(() -> fetchAndCache(owner, repo));
    }

    private GithubRepositoryResponseDTO fetchAndCache(String owner, String repo) {
        GithubApiResponseDTO apiResponse =
                githubApiClient.fetchRepository(owner, repo);

        RepositoryCacheEntity entity = new RepositoryCacheEntity();
        entity.setOwner(owner);
        entity.setName(repo);
        entity.setFullName(apiResponse.getFullName());
        entity.setDescription(apiResponse.getDescription());
        entity.setCloneUrl(apiResponse.getCloneUrl());
        entity.setStars(apiResponse.getStargazersCount());
        entity.setCreatedAt(apiResponse.getCreatedAt());
        entity.setCachedAt(Instant.now());

        cacheRepository.save(entity);

        return toResponse(entity);
    }

    private GithubRepositoryResponseDTO toResponse(RepositoryCacheEntity e) {
        return new GithubRepositoryResponseDTO(
                e.getFullName(),
                e.getDescription(),
                e.getCloneUrl(),
                e.getStars(),
                e.getCreatedAt()
        );
    }
}
