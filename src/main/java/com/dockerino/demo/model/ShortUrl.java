package com.dockerino.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ShortUrl {

    private Long id; // Assuming this was BIGSERIAL, so Long
    private String shortCode;
    private String originalUrl;
    private UUID userId; // Store user's ID directly
    private LocalDateTime createdAt;

    public ShortUrl(String shortCode, String originalUrl, UUID userId) {
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.userId = userId;
    }

    public ShortUrl(Long id, String shortCode, String originalUrl, UUID userId, LocalDateTime createdAt) {
        this.id = id;
        this.shortCode = shortCode;
        this.originalUrl = originalUrl;
        this.userId = userId;
        this.createdAt = createdAt;
    }
}