package com.jiajin.simplerestserver.infrastructure.dao;

import com.jiajin.simplerestserver.infrastructure.entity.RepositoryCacheHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ljj
 * @create 2026 01 10 22:11
 */
@Repository
public interface RepositoryHistoryRepository
        extends JpaRepository<RepositoryCacheHistoryEntity, Long> {

    List<RepositoryCacheHistoryEntity> findByOwnerAndRepoNameOrderByArchivedAtDesc(String owner, String repoName);
}
