package com.dockerino.demo.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public record ShortUrlRequest(
        @NotNull
        @NotBlank
        @URL(message = "Please provide a valid URL")
        String originalUrl
) {
}
