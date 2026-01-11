package com.jiajin.simplerestserver.controller;

import com.jiajin.simplerestserver.controller.dto.GithubRepositoryResponseDTO;
import com.jiajin.simplerestserver.service.GithubRepositoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ljj
 * @create 2026 01 09 20:03
 */
@Slf4j
@RestController
@RequestMapping("/repositories")
public class RepositoryController {
    private final GithubRepositoryService service;

    public RepositoryController(GithubRepositoryService service) {
        this.service = service;
    }

    @GetMapping("/{owner}/{repo}")
    public GithubRepositoryResponseDTO getRepository(
            @PathVariable String owner,
            @PathVariable("repo") String repositoryName) {

        log.info("Incoming request: {}/{}", owner, repositoryName);
        return service.getRepository(owner, repositoryName);
    }
}
