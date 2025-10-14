package com.dockerino.demo.api;

import com.dockerino.demo.model.dtos.*;
import com.dockerino.demo.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationApi {

    private final AuthenticationService authenticationService;

    public AuthenticationApi(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<BasicLoginResponse> login(@Valid @RequestBody BasicLoginRequest basicLoginRequest) {
        BasicLoginResponse basicLoginResponse = authenticationService.loginUser(basicLoginRequest);
        return ResponseEntity.ok(basicLoginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        RegisterUserResponse registerUserResponse = authenticationService.registerUser(registerUserRequest);
        return ResponseEntity.ok(registerUserResponse);
    }
}
