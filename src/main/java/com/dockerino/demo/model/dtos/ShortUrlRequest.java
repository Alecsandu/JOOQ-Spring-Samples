package com.dockerino.demo.model.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record ShortUrlRequest(
        @NotNull
        @URL(message = "Please provide a valid URL")
        @Size(min = 4)
        String originalUrl
) {
}
