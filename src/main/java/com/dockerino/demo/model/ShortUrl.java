package com.dockerino.demo.model;


import java.time.LocalDateTime;
import java.util.UUID;

public record ShortUrl(
        Long id,
        String shortCode,
        String originalUrl,
        UUID userId,
        LocalDateTime createdAt
) {
}