package com.dockerino.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class User {

    private UUID id;

    private String email;

    private String password;

    private String username;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}