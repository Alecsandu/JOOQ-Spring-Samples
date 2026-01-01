package com.dockerino.demo.service;

import com.dockerino.demo.config.properties.DomainProperties;
import com.dockerino.demo.exception.authentication.AuthenticationException;
import com.dockerino.demo.exception.authentication.UserAlreadyExistsException;
import com.dockerino.demo.model.User;
import com.dockerino.demo.model.dtos.BasicLoginRequest;
import com.dockerino.demo.model.dtos.BasicLoginResponse;
import com.dockerino.demo.model.dtos.RegisterUserRequest;
import com.dockerino.demo.model.dtos.RegisterUserResponse;
import com.dockerino.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthenticationService {

    private static final Long TOKEN_VALID_TIME = 5L;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtEncoder jwtEncoder;

    private final DomainProperties domainProperties;

    public AuthenticationService(
            UserRepository userRepository, PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder, DomainProperties domainProperties
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.domainProperties = domainProperties;
    }

    public BasicLoginResponse loginUser(BasicLoginRequest basicLoginRequest) {
        User user = userRepository.findByEmail(basicLoginRequest.email());

        if (!passwordEncoder.matches(basicLoginRequest.password(), user.password())) {
            throw new AuthenticationException("Invalid credentials");
        }

        return new BasicLoginResponse(generateToken(user));
    }

    @Transactional
    public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest) {
        if (userRepository.existsByEmail(registerUserRequest.email())) {
            throw new UserAlreadyExistsException("Account with given email already exists");
        }

        if (userRepository.existsByUsername(registerUserRequest.username())) {
            throw new UserAlreadyExistsException("Account with given username already exists");
        }

        return userRepository.save(registerUserRequest);
    }

    private String generateToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.id().toString())
                .issuedAt(now)
                .expiresAt(now.plus(TOKEN_VALID_TIME, ChronoUnit.MINUTES))
                .issuer(domainProperties.root())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
