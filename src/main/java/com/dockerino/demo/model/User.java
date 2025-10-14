package com.dockerino.demo.model;

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
}
