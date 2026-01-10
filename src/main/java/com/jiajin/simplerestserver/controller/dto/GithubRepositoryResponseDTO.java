package com.jiajin.simplerestserver.controller.dto;

import com.jiajin.simplerestserver.infrastructure.entity.RepositoryCacheEntity;

import java.time.Instant;

/**
 * Github repository
 * @author ljj
 * @create 2026 01 09 20:12
 */
public record GithubRepositoryResponseDTO(
        /**
         * Full name of the repository
         */
        String fullName,
        /**
         * Description of the repository
         */
        String description,
        /**
         * Git clone url
         */
        String cloneUrl,
        /**
         * Number of stars
         */
        int stars,
        /**
         * Date of creation
         */
        Instant createdAt
){
    public static GithubRepositoryResponseDTO from(RepositoryCacheEntity entity) {
        return new GithubRepositoryResponseDTO(
                entity.getFullName(),
                entity.getDescription(),
                entity.getCloneUrl(),
                entity.getStars(),
                entity.getCreatedAt()
        );
    }
}
