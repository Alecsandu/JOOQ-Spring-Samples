package com.dockerino.demo.model;

import org.jspecify.annotations.NonNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record User(
        UUID id,
        String email,
        String password,
        String username,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    @Override
    @NonNull
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + "****" + '\'' +
                ", username='" + username + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
