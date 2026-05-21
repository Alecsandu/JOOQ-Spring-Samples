package com.dockerino.demo.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record User(
        UUID id,
        String auth0_sub,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        Boolean isActive
) {
}
