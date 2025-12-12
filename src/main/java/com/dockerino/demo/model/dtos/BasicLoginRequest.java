package com.dockerino.demo.model.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.jspecify.annotations.NonNull;

public record BasicLoginRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Length(min = 12, max = 128)
        String password
) {
    @Override
    @NonNull
    public String toString() {
        return "BasicLoginRequest{" +
                "email='" + email + '\'' +
                ", password='" + "****" + '\'' +
                '}';
    }
}
