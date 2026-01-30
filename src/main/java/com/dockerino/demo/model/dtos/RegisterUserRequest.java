package com.dockerino.demo.model.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import org.jspecify.annotations.NonNull;

public record RegisterUserRequest(
        @NotNull
        @Email
        @Size(min = 10, max = 50)
        String email,

        @NotNull
        @Length(min = 14, max = 128)
        String password,

        @NotNull
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
