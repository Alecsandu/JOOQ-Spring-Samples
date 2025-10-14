package com.dockerino.demo.model.dtos;

import java.util.UUID;

public record UserInfo(UUID id, String email, String name) {
}
