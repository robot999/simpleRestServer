package com.jiajin.simplerestserver.infrastructure.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author ljj
 * @create 2026 01 10 22:07
 */
@Entity
@Table(name = "repository_cache_history",
        indexes = {
                @Index(name = "idx_repo_history", columnList = "owner,repo_name")
        })
public class RepositoryCacheHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String owner;

    @Column(name = "repo_name")
    private String repoName;

    private String description;
    private int stars;

    private LocalDateTime cachedAt;
    private LocalDateTime archivedAt;

    public static RepositoryCacheHistoryEntity from(RepositoryCacheEntity cache) {
        RepositoryCacheHistoryEntity history = new RepositoryCacheHistoryEntity();
        history.owner = cache.getOwner();
        history.repoName = cache.getFullName();
        history.description = cache.getDescription();
        history.stars = cache.getStars();
        history.cachedAt = LocalDateTime.ofInstant(cache.getCachedAt(), ZoneOffset.UTC);
        history.archivedAt = LocalDateTime.now();
        return history;
    }
}
