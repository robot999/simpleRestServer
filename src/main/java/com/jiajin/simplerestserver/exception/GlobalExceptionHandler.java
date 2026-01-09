package com.jiajin.simplerestserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author ljj
 * @create 2026 01 09 21:16
 */
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(RepositoryNotFoundException.class)
    public ResponseEntity<String> handleRepoNotFound(
            RepositoryNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(GithubRateLimitException.class)
    public ResponseEntity<String> handleRateLimit(
            GithubRateLimitException ex) {

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS) // 429
                .body(ex.getMessage());
    }
}
