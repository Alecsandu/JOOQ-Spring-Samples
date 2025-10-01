package com.dockerino.demo.model.dtos;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record ShortUrlRequest(
        @NotBlank
        @URL(message = "Please provide a valid URL")
        String originalUrl
) {
}