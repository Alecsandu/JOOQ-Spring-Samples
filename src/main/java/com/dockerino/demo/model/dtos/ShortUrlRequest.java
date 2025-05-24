package com.dockerino.demo.model.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class ShortUrlRequest {
    @NotBlank
    @URL(message = "Please provide a valid URL")
    private String originalUrl;
}