package com.dockerino.demo.model.dtos;

import java.util.UUID;

public record RegisterResponse(UUID id, String email, String name) {
}
