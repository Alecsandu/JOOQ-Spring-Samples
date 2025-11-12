package com.dockerino.demo.api;

import com.dockerino.demo.model.dtos.BasicLoginRequest;
import com.dockerino.demo.model.dtos.BasicLoginResponse;
import com.dockerino.demo.model.dtos.RegisterUserRequest;
import com.dockerino.demo.model.dtos.RegisterUserResponse;
import com.dockerino.demo.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationApi {

    private final AuthenticationService authenticationService;

    public AuthenticationApi(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody BasicLoginRequest basicLoginRequest) {
        BasicLoginResponse basicLoginResponse = authenticationService.loginUser(basicLoginRequest);

        var cookie = ResponseCookie.from("access_token", basicLoginResponse.accessToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Login successful!");
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        RegisterUserResponse registerUserResponse = authenticationService.registerUser(registerUserRequest);
        return ResponseEntity.ok(registerUserResponse);
    }
}
