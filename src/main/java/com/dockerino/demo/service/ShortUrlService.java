package com.dockerino.demo.service;

import com.dockerino.demo.exception.InvalidTokenException;
import com.dockerino.demo.exception.UserNotFoundException;
import com.dockerino.demo.model.ShortUrl;
import com.dockerino.demo.model.User;
import com.dockerino.demo.model.dtos.ShortUrlResponse;
import com.dockerino.demo.repository.ShortUrlRepository;
import com.dockerino.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShortUrlService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int SHORT_CODE_LENGTH = 8;

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final JwtDecoder jwtDecoder;

    public ShortUrlService(ShortUrlRepository shortUrlRepository, UserRepository userRepository, JwtDecoder jwtDecoder) {
        this.shortUrlRepository = shortUrlRepository;
        this.userRepository = userRepository;
        this.jwtDecoder = jwtDecoder;
    }

    @Transactional
    public ShortUrlResponse createShortUrl(String originalUrl, HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        String shortCode;
        do {
            shortCode = generateShortCode();
        } while (shortUrlRepository.existsByShortCode(shortCode));

        ShortUrl shortUrl = new ShortUrl(shortCode, originalUrl, user.getId());
        shortUrl = shortUrlRepository.save(shortUrl);

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        return new ShortUrlResponse(shortCode, originalUrl, baseUrl + "/" + shortUrl.getShortCode());
    }

    @Transactional(readOnly = true)
    public Optional<String> getOriginalUrl(String shortCode) {
        return shortUrlRepository.findByShortCode(shortCode)
                .map(ShortUrl::getOriginalUrl);
    }

    @Transactional(readOnly = true)
    public List<ShortUrlResponse> getUserUrls(HttpServletRequest request) {
        UUID userId = getUserIdFromToken(request);

        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        return shortUrlRepository.findByUserId(user.getId()).stream()
                .map(su -> new ShortUrlResponse(su.getShortCode(), su.getOriginalUrl(), baseUrl + "/" + su.getShortCode()))
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

    private String generateShortCode() {
        byte[] bytes = new byte[SHORT_CODE_LENGTH];
        RANDOM.nextBytes(bytes);
        String base64Encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return base64Encoded.substring(0, Math.min(base64Encoded.length(), SHORT_CODE_LENGTH));
    }
}
