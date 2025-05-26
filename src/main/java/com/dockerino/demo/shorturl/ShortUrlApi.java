package com.dockerino.demo.shorturl;

import com.dockerino.demo.model.dtos.ShortUrlResponse;
import com.dockerino.demo.model.dtos.ShortUrlRequest;
import com.dockerino.demo.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class ShortUrlApi {

    @Autowired
    private ShortUrlService shortUrlService;

    @PostMapping("/api/urls")
    public ResponseEntity<ShortUrlResponse> shortenUrl(
            @Valid @RequestBody ShortUrlRequest shortenRequest,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            HttpServletRequest request
    ) {
        ShortUrlResponse response = shortUrlService.createShortUrl(
                shortenRequest.getOriginalUrl(),
                currentUser.getId(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
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
    @GetMapping("/api/urls/my-urls")
    public ResponseEntity<List<ShortUrlResponse>> getMyUrls(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            HttpServletRequest request
    ) {
        List<ShortUrlResponse> myUrls = shortUrlService.getUserUrls(currentUser.getId(), request);
        return ResponseEntity.ok(myUrls);
    }
}