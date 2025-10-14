package com.dockerino.demo.model.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record BasicLoginRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Length(min = 12, max = 128)
        String password
) {
}
