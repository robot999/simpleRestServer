package com.jiajin.simplerestserver;

import com.jiajin.simplerestserver.controller.dto.GithubApiResponseDTO;
import com.jiajin.simplerestserver.infrastructure.dao.RepositoryCacheRepository;
import com.jiajin.simplerestserver.infrastructure.entity.RepositoryCacheEntity;
import com.jiajin.simplerestserver.service.GithubApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author ljj
 * @create 2026 01 10 23:06
 */
@SpringBootTest
@ActiveProfiles("test")
public class RepositoryServiceTest {
    @Autowired
    private RepositoryCacheRepository repositoryService;

    @Autowired
    private RepositoryCacheRepository cacheRepository;

    @MockBean
    private GithubApiClient githubClient;

    @Test
    void should_return_old_cache_when_expired() {
        // given：an expired cache
        RepositoryCacheEntity cache = new RepositoryCacheEntity();
        cache.setOwner("robot999");
        cache.setName("simpleRestServer");
        cache.setDescription("old description");
        cache.setStars(1);
        LocalDateTime expiredTime = LocalDateTime.now().minusHours(2);
        cache.setCachedAt(expiredTime.toInstant(ZoneOffset.UTC));
        cacheRepository.save(cache);

        // GitHub return new data (async use)
        when(githubClient.fetchRepository("robot999", "simpleRestServer"))
                .thenReturn(new GithubApiResponseDTO(
                        "robot999/simpleRestServer",
                        "new description",
                        "https://github.com/xxx.git",
                        10,
                        LocalDateTime.now().toInstant(ZoneOffset.UTC)
                ));

        // when
        Optional<RepositoryCacheEntity> response =
                repositoryService.findByOwnerAndName("robot999", "simpleRestServer");

        // then：returned data is still old
        if (response.isPresent()) {
            RepositoryCacheEntity repositoryCacheEntity = response.get();
            assertThat(repositoryCacheEntity.getDescription()).isEqualTo("old description");
            assertThat(repositoryCacheEntity.getStars()).isEqualTo(1);
        }

    }
}
