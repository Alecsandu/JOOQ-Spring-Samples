package com.dockerino.demo.model;


import java.time.OffsetDateTime;
import java.util.UUID;

public record ShortUrl(
        Long id,
        String shortCode,
        String originalUrl,
        UUID userId,
        OffsetDateTime createdAt
) {
}
