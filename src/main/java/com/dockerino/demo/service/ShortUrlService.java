package com.dockerino.demo.service;

import com.dockerino.demo.config.UrlProperties;
import com.dockerino.demo.exception.UserNotFoundException;
import com.dockerino.demo.exception.authentication.InvalidTokenException;
import com.dockerino.demo.model.ShortUrl;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final JwtDecoder jwtDecoder;
    private final UrlProperties urlProperties;

    public ShortUrlService(ShortUrlRepository shortUrlRepository, UserRepository userRepository,
                           JwtDecoder jwtDecoder, UrlProperties urlProperties
    ) {
        this.shortUrlRepository = shortUrlRepository;
        this.userRepository = userRepository;
        this.jwtDecoder = jwtDecoder;
        this.urlProperties = urlProperties;
    }

    @Transactional
    public ShortUrlResponse createShortUrl(String originalUrl, HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        ShortUrl shortUrl = shortUrlRepository.save(originalUrl, userId);
        String shortcode = Base32.encode(shortUrl.id().toString(), false);

        String baseUrl = urlProperties.getUrl() + shortcode;
        return new ShortUrlResponse(originalUrl, baseUrl);
    }

    public String getOriginalUrl(String shortCode) {
        try {
            Long code = Long.valueOf(Base32.decode(shortCode, false));
            return shortUrlRepository.findByShortCode(code).originalUrl();
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid url");
        }
    }

    public List<ShortUrlResponse> getUserUrls(HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        return shortUrlRepository.findByUserId(userId)
                .stream()
                .map(su -> {
                    String shortcode = Base32.encode(su.id().toString(), false);
                    return new ShortUrlResponse(su.originalUrl(), urlProperties.getUrl() + shortcode);
                })
                .collect(Collectors.toList());
    }

    private UUID getUserIdFromToken(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);

        Jwt jwt = jwtDecoder.decode(token);

        return UUID.fromString(jwt.getClaim("sub"));
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken.isBlank() || !bearerToken.startsWith("Bearer ")) {
            throw new InvalidTokenException();
        }

        return bearerToken.substring(7);
    }
}
