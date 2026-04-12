package com.dockerino.demo.service;

import com.dockerino.demo.exception.UserNotFoundException;
import com.dockerino.demo.exception.authentication.InvalidTokenException;
import com.dockerino.demo.model.User;
import com.dockerino.demo.model.dtos.ShortUrlResponse;
import com.dockerino.demo.repository.ShortUrlRepository;
import com.dockerino.demo.repository.UserRepository;
import in.co.tasky.Base32;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;

    private final UserRepository userRepository;

    private final JwtDecoder jwtDecoder;

    public ShortUrlService(
            ShortUrlRepository shortUrlRepository,
            UserRepository userRepository,
            JwtDecoder jwtDecoder
    ) {
        this.shortUrlRepository = shortUrlRepository;
        this.userRepository = userRepository;
        this.jwtDecoder = jwtDecoder;
    }

    @Transactional
    public ShortUrlResponse createShortUrl(String originalUrl, HttpServletRequest request) {
        String auth0Sub = getUserSubFromToken(request);

        User user = userRepository.findUserBySub(auth0Sub)
                .orElseThrow(UserNotFoundException::new);

        Long shortUrlId = shortUrlRepository.save(originalUrl, user.id());

        String shortcode = Base32.getEnDer(Base32.Encoding.CROCKFORD).encode(shortUrlId.toString(), false);

        return new ShortUrlResponse(originalUrl, shortcode);
    }

    public String getOriginalUrl(String shortCode) {
        try {
            Long code = Long.valueOf(Base32.getEnDer(Base32.Encoding.CROCKFORD).decode(shortCode, false));
            return shortUrlRepository.findByShortCode(code).originalUrl();
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid url");
        }
    }

    public List<ShortUrlResponse> getUserUrls(HttpServletRequest request) {
        String auth0Sub = getUserSubFromToken(request);

        if (!userRepository.existsByAuth0Sub(auth0Sub)) {
            throw new UserNotFoundException();
        }

        User user = userRepository.findUserBySub(auth0Sub).orElseThrow(UserNotFoundException::new);

        return shortUrlRepository.findAllByUserId(user.id())
                .map(su -> {
                    String shortcode = Base32.getEnDer(Base32.Encoding.CROCKFORD).encode(su.id().toString(), false);
                    return new ShortUrlResponse(su.originalUrl(), shortcode);
                })
                .collect(Collectors.toList());
    }

    private String getUserSubFromToken(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);

        Jwt jwt = jwtDecoder.decode(token);

        return jwt.getClaim("sub");
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken.isBlank() || !bearerToken.startsWith("Bearer ")) {
            throw new InvalidTokenException();
        }

        return bearerToken.substring(7);
    }
}
