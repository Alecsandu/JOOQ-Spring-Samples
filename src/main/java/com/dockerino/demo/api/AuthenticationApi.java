package com.dockerino.demo.api;

import com.dockerino.demo.config.util.IsUser;
import com.dockerino.demo.model.dtos.BasicLoginRequest;
import com.dockerino.demo.model.dtos.BasicLoginResponse;
import com.dockerino.demo.model.dtos.RegisterUserRequest;
import com.dockerino.demo.model.dtos.RegisterUserResponse;
import com.dockerino.demo.service.AuthenticationService;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationApi {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationApi.class);
    private final AuthenticationService authenticationService;

    public AuthenticationApi(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @IsUser
    public ResponseEntity<@NonNull BasicLoginResponse> login(
            @Valid @RequestBody BasicLoginRequest request
    ) {
        log.debug("Login attempt for email={}", request.email());

        BasicLoginResponse basicLoginResponse = authenticationService.loginUser(request);

        return ResponseEntity.ok(basicLoginResponse);
    }

    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @IsUser
    public ResponseEntity<@NonNull RegisterUserResponse> register(
            @Valid @RequestBody RegisterUserRequest request
    ) {
        log.debug("Registration attempt for email={}", request.email());

        RegisterUserResponse registerUserResponse = authenticationService.registerUser(request);

        return ResponseEntity.ok(registerUserResponse);
    }
}
