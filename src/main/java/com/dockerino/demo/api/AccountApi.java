package com.dockerino.demo.api;

import com.dockerino.demo.model.User;
import com.dockerino.demo.model.dtos.AuthResponse;
import com.dockerino.demo.model.dtos.LoginRequest;
import com.dockerino.demo.model.dtos.RegisterRequest;
import com.dockerino.demo.model.dtos.UserInfo;
import com.dockerino.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AccountApi {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.loginUser(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User result = authService.registerUser(registerRequest);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/api/users/{username}") // Or a profile endpoint
                    .buildAndExpand(result.getEmail()).toUri(); // Using email as example

            // Just return created status, might return token in the future to immediately log in the user.
            return ResponseEntity.created(location)
                    .body(new UserInfo(result.getId(), result.getEmail(), result.getUsername()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}