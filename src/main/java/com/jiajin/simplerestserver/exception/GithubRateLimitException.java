package com.jiajin.simplerestserver.exception;

/**
 * @author ljj
 * @create 2026 01 09 21:18
 */
public class GithubRateLimitException extends RuntimeException {

    public GithubRateLimitException() {
        super("GitHub API rate limit exceeded. Please try again later.");
    }

    public GithubRateLimitException(String message) {
        super(message);
    }
}
