package com.dockerino.demo.model.dtos;

public record ShortUrlResponse(
        String shortCode,
        String originalUrl,
        String fullShortUrl
) {
}
