package com.dockerino.demo.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShortUrlResponse {
    private String shortCode;
    private String originalUrl;
    private String fullShortUrl; // e.g., http://localhost:6868/XyZ123
}