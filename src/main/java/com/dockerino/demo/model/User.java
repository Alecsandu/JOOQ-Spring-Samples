package com.dockerino.demo.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record User(
        UUID id,
        String email,
        String password,
        String username,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
