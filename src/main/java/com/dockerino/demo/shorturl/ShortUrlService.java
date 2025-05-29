package com.dockerino.demo.shorturl;

import com.dockerino.demo.model.ShortUrl;
import com.dockerino.demo.model.User;
import com.dockerino.demo.model.dtos.ShortUrlResponse;
import com.dockerino.demo.repository.ShortUrlRepository;
import com.dockerino.demo.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShortUrlService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int SHORT_CODE_LENGTH = 8;

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;

    public ShortUrlService(ShortUrlRepository shortUrlRepository, UserRepository userRepository) {
        this.shortUrlRepository = shortUrlRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ShortUrlResponse createShortUrl(String originalUrl, UUID userId, HttpServletRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

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
    public String getOriginalUrl(String shortCode) {
        return shortUrlRepository.findByShortCode(shortCode)
                .map(ShortUrl::getOriginalUrl)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ShortUrlResponse> getUserUrls(UUID userId, HttpServletRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        return shortUrlRepository.findByUserOrderByCreatedAtDesc(user.getId()).stream()
                .map(su -> new ShortUrlResponse(su.getShortCode(), su.getOriginalUrl(), baseUrl + "/" + su.getShortCode()))
                .collect(Collectors.toList());
    }


    private String generateShortCode() {
        byte[] bytes = new byte[SHORT_CODE_LENGTH]; // Will generate more than needed due to base64 padding
        RANDOM.nextBytes(bytes);
        // Use URL-safe Base64 encoding and take the first SHORT_CODE_LENGTH characters
        String base64Encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return base64Encoded.substring(0, Math.min(base64Encoded.length(), SHORT_CODE_LENGTH));
    }
}