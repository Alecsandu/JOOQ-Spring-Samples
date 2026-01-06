package com.dockerino.demo.model.dtos;

public record ShortUrlResponse(
        String originalUrl,
        String shortcode
) {
}
