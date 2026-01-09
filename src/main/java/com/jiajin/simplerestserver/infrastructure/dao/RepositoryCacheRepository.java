package com.jiajin.simplerestserver.infrastructure.dao;

import com.jiajin.simplerestserver.infrastructure.entity.RepositoryCacheEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author ljj
 * @create 2026 01 09 20:21
 */
public interface  RepositoryCacheRepository extends JpaRepository<RepositoryCacheEntity, Long> {
    /**
     * Finds a RepositoryCacheEntity by owner and repository name.
     * @param owner
     * @param repoName
     * @return
     */
    Optional<RepositoryCacheEntity> findByOwnerAndName(
            String owner,
            String repoName
    );
}
