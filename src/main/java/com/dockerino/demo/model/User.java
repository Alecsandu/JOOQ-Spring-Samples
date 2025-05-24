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

    private String password; // Nullable for OAuth2 users

    private AuthProvider provider;

    private String providerId; // e.g., Google's subject ID

    private String username; // Name from Google or set manually

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public User(String email, String password, String username, AuthProvider provider) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.provider = provider;
    }
}