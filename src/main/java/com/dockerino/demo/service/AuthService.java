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
public class AuthService {

    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public AuthService(CustomUserDetailsService customUserDetailsService, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    @Transactional
    public BasicLoginResponse loginUser(BasicLoginRequest basicLoginRequest) {
        CustomUserDetails userDetails;
        try {
            userDetails = ((CustomUserDetails) customUserDetailsService.loadUserByUsername(basicLoginRequest.getEmail()));
        } catch (UsernameNotFoundException e) {
            throw new AuthenticationException(e);
        }

        if (!passwordEncoder.matches(basicLoginRequest.getPassword(), userDetails.getPassword())) {
            throw new AuthenticationException("Invalid credentials!");
        }

        UserInfo userInfo = new UserInfo(userDetails.getId(), userDetails.getEmail(), userDetails.getUsername());

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(userInfo.id().toString())
                .issuer("http://localhost:8080")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .claim("scope", extractPermissions(userDetails))
                .build();

        String jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new BasicLoginResponse(jwt, userInfo);
    }

    private String extractPermissions(UserDetails userDetails) {
        return userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }

    @Transactional
    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new AuthenticationException("Email address already in use.");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setUsername(registerRequest.getUsername());

        User createdUser = userRepository.save(user);

        return new RegisterResponse(createdUser.getId(), createdUser.getEmail(), createdUser.getUsername());
    }
}