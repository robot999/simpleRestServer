package com.jiajin.simplerestserver.service;

import com.jiajin.simplerestserver.controller.dto.GithubApiResponseDTO;
import com.jiajin.simplerestserver.controller.dto.GithubRepositoryResponseDTO;
import com.jiajin.simplerestserver.infrastructure.dao.RepositoryCacheRepository;
import com.jiajin.simplerestserver.infrastructure.dao.RepositoryHistoryRepository;
import com.jiajin.simplerestserver.infrastructure.entity.RepositoryCacheEntity;
import com.jiajin.simplerestserver.infrastructure.entity.RepositoryCacheHistoryEntity;
import com.jiajin.simplerestserver.util.TransactionExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ljj
 * @create 2026 01 09 20:04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GithubRepositoryService {
    private static final Duration TTL = Duration.ofMinutes(30);

    private final RepositoryCacheRepository cacheRepository;
    private final RepositoryHistoryRepository historyRepository;
    private final GithubApiClient githubApiClient;
    //todo
    private final ApplicationEventPublisher eventPublisher;
    private final TransactionExecutor transactionExecutor;

    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    public GithubRepositoryResponseDTO getRepository(String owner, String repo) {
        String key = owner + "/" + repo;
        Optional<RepositoryCacheEntity> optional = cacheRepository.findByOwnerAndName(owner, repo);

        // cache exists
        if (optional.isPresent()) {
            RepositoryCacheEntity cache = optional.get();

            if (isExpired(cache)) {
                // expired: asynchronous refresh
                refreshAsync(owner, repo, cache);
            }

            // key point: directly return the cache regardless of whether expired or not
            return GithubRepositoryResponseDTO.from(cache);
        }

        //  cache Not Found (to prevent cache breakdown)
        synchronized (locks.computeIfAbsent(key, k -> new Object())) {
            // double check
            return cacheRepository.findByOwnerAndName(owner, repo)
                    .map(this::toResponse)
                    .orElseGet(() -> fetchAndSave(owner, repo));
        }
    }

    private GithubRepositoryResponseDTO fetchAndSave(String owner, String repo) {
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

    private boolean isExpired(RepositoryCacheEntity cache) {
        return cache.getCachedAt()
                .isBefore(LocalDateTime.now().minus(TTL).toInstant(ZoneOffset.UTC));
    }

    @Async
    protected void refreshAsync(String owner, String repo, RepositoryCacheEntity oldCache) {
        doRefreshAsync(owner, repo, oldCache);
    }

    private void doRefreshAsync(String owner, String repo, RepositoryCacheEntity oldCache) {
        try {
            GithubApiResponseDTO latest = githubApiClient.fetchRepository(owner, repo);

            if (hasChanged(oldCache, latest)) {
                historyRepository.save(
                        RepositoryCacheHistoryEntity.from(oldCache)
                );
            }

            oldCache.updateFrom(latest);
            transactionExecutor.executeRequireNew(()->{
                cacheRepository.save(oldCache);
            });

        } catch (Exception ex) {
            log.warn("Async refresh failed for {}/{}", owner, repo, ex);
        }
    }

    private boolean hasChanged(RepositoryCacheEntity cache, GithubApiResponseDTO latest) {
        return !Objects.equals(cache.getDescription(), latest.getDescription())
                || cache.getStars() != latest.getStargazersCount();
    }
}
