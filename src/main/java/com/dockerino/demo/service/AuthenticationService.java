package com.dockerino.demo.service;

import com.dockerino.demo.exception.AuthenticationException;
import com.dockerino.demo.model.User;
import com.dockerino.demo.model.dtos.*;
import com.dockerino.demo.repository.UserRepository;
import com.dockerino.demo.security.CustomUserDetails;
import com.dockerino.demo.security.CustomUserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    private static final Long TOKEN_VALID_TIME = 5L;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public AuthenticationService(CustomUserDetailsService customUserDetailsService, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    @Transactional
    public BasicLoginResponse loginUser(BasicLoginRequest basicLoginRequest) {
        CustomUserDetails userDetails;
        try {
            userDetails = ((CustomUserDetails) customUserDetailsService.loadUserByUsername(basicLoginRequest.email()));
        } catch (UsernameNotFoundException e) {
            throw new AuthenticationException(e);
        }

        if (!passwordEncoder.matches(basicLoginRequest.password(), userDetails.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(userDetails.email())
                .id(userDetails.id().toString())
                .issuedAt(now)
                .expiresAt(now.plus(TOKEN_VALID_TIME, ChronoUnit.MINUTES))
                .claim("scope", extractPermissions(userDetails))
                .build();
        String jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new BasicLoginResponse(jwt);
    }

    private String extractPermissions(UserDetails userDetails) {
        return userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }

    @Transactional
    public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest) {
        if (userRepository.existsByEmail(registerUserRequest.email())) {
            throw new AuthenticationException("Email is already taken");
        }

        User createdUser = userRepository.save(registerUserRequest);

        return new RegisterUserResponse(createdUser.id(), createdUser.email(), createdUser.username());
    }
}