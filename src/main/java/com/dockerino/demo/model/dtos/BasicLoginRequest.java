package com.dockerino.demo.model.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import org.jspecify.annotations.NonNull;

public record BasicLoginRequest(
        @NotNull
        @Email
        @Size(min = 10, max = 50)
        String email,

        @NotNull
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
