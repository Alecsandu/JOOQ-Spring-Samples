package com.dockerino.demo.api;

import com.dockerino.demo.model.dtos.*;
import com.dockerino.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AccountApi {

    private final AuthService authService;

    public AccountApi(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<BasicLoginResponse> login(@Valid @RequestBody BasicLoginRequest basicLoginRequest) {
        BasicLoginResponse basicLoginResponse = authService.loginUser(basicLoginRequest);
        return ResponseEntity.ok(basicLoginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse registerResponse = authService.registerUser(registerRequest);
        return ResponseEntity.ok(registerResponse);
    }
}