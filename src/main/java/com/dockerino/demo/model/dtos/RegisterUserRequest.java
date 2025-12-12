package com.dockerino.demo.model.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.jspecify.annotations.NonNull;

public record RegisterUserRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        @Length(min = 14, max = 128)
        String password,

        @NotBlank
        @Length(min = 5, max = 255)
        String username
) {
        @Override
        @NonNull
        public String toString() {
                return "RegisterUserRequest{" +
                        "email='" + email + '\'' +
                        ", password='" + "****" + '\'' +
                        ", username='" + username + '\'' +
                        '}';
        }
}
