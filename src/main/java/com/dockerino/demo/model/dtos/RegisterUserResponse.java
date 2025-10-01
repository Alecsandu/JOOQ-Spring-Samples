package com.dockerino.demo.model.dtos;

import java.util.UUID;

public record RegisterUserResponse(UUID id, String email, String name) {
}
