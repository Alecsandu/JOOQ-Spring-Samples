package com.dockerino.demo.api;

import com.dockerino.demo.model.dtos.*;
import com.dockerino.demo.service.AuthenticationService;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationApi {

    private final AuthenticationService authenticationService;

    private static final Logger log = LoggerFactory.getLogger(AuthenticationApi.class);

    public AuthenticationApi(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<@NonNull BasicLoginResponse> login(@Valid @RequestBody BasicLoginRequest basicLoginRequest) {
        BasicLoginResponse basicLoginResponse = authenticationService.loginUser(basicLoginRequest);
        log.info("Successfully authenticated user {}", basicLoginRequest.email());
        return ResponseEntity.ok(basicLoginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<@NonNull RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        RegisterUserResponse registerUserResponse = authenticationService.registerUser(registerUserRequest);
        return ResponseEntity.ok(registerUserResponse);
    }
}
