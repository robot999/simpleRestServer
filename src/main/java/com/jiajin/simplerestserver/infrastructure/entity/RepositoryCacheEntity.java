package com.jiajin.simplerestserver.infrastructure.entity;

import com.jiajin.simplerestserver.controller.dto.GithubApiResponseDTO;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * table save github repository info
 * @author jiajin
 * @date 2026/1/09
 */
@Entity
@Table(name = "repository_cache",
        uniqueConstraints = @UniqueConstraint( name = "uk_owner_repo",columnNames = {"owner", "repo_name"}))
public class RepositoryCacheEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String owner;

    @Column(name = "repo_name", nullable = false, length = 200)
    private String name;

    @Column(name = "full_name")
    private String fullName;

    @Column(length = 1000)
    private String description;

    @Column(name = "clone_url")
    private String cloneUrl;

    @Column(name = "stars")
    private int stars;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "cached_at", nullable = false)
    private Instant cachedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCloneUrl() {
        return cloneUrl;
    }

    public void setCloneUrl(String cloneUrl) {
        this.cloneUrl = cloneUrl;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getCachedAt() {
        return cachedAt;
    }

    public void setCachedAt(Instant cachedAt) {
        this.cachedAt = cachedAt;
    }

    public void updateFrom(GithubApiResponseDTO latest) {

        // Mutable Fields
        this.fullName = latest.getFullName();
        this.description = latest.getDescription();
        this.cloneUrl = latest.getCloneUrl();
        this.stars = latest.getStargazersCount();

        if (this.createdAt == null) {
            this.createdAt = latest.getCreatedAt();
        }

        // refresh cached time
        this.cachedAt = Instant.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
    }
}
