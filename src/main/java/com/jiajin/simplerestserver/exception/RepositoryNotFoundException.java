package com.jiajin.simplerestserver.exception;

/**
 * @author ljj
 * @create 2026 01 09 21:14
 */
public class RepositoryNotFoundException extends RuntimeException {


    public RepositoryNotFoundException(String owner, String repo) {
        super("Repository not found: " + owner + "/" + repo);
    }
}