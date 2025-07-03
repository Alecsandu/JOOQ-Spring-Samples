package com.dockerino.demo.shorturl;

import com.dockerino.demo.model.dtos.ShortUrlRequest;
import com.dockerino.demo.model.dtos.ShortUrlResponse;
import com.dockerino.demo.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/url")
public class ShortUrlApi {

    private final ShortUrlService shortUrlService;

    public ShortUrlApi(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @PostMapping
    public ResponseEntity<ShortUrlResponse> createShortUrl(
            @Valid @RequestBody ShortUrlRequest shortenRequest, @AuthenticationPrincipal CustomUserDetails currentUser, HttpServletRequest request
    ) {
        ShortUrlResponse response = shortUrlService.createShortUrl(shortenRequest.getOriginalUrl(), currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> getOriginalUrlByShortCode(@PathVariable String shortCode) {
        String originalUrl = shortUrlService.getOriginalUrl(shortCode);

        if (originalUrl == null) {
            return ResponseEntity.notFound().build();
        }
        if (!originalUrl.matches("^https?://.*")) {
            originalUrl = "https://" + originalUrl;
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .build();
    }

    @Secured({"ROLE_USER"})
    @GetMapping("/all")
    public ResponseEntity<List<ShortUrlResponse>> getShortUrlsByUserId(
            @AuthenticationPrincipal CustomUserDetails currentUser, HttpServletRequest request
    ) {
        List<ShortUrlResponse> urlsByUserId = shortUrlService.getUserUrls(currentUser.getId(), request);
        return ResponseEntity.ok(urlsByUserId);
    }
}