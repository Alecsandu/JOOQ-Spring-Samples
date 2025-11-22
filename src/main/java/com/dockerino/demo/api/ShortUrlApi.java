package com.dockerino.demo.api;

import com.dockerino.demo.model.dtos.ShortUrlRequest;
import com.dockerino.demo.model.dtos.ShortUrlResponse;
import com.dockerino.demo.service.ShortUrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<@NonNull ShortUrlResponse> createShortUrl(@Valid @RequestBody ShortUrlRequest shortenRequest, HttpServletRequest request) {
        ShortUrlResponse response = shortUrlService.createShortUrl(shortenRequest.originalUrl(), request);
        return ResponseEntity.created(URI.create(response.fullShortUrl())).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<@NonNull Void> getOriginalUrlByShortCode(@PathVariable String shortCode) {
        String originalUrl = shortUrlService.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .location(URI.create(originalUrl))
                .build();
    }

    @GetMapping("/all")
    public ResponseEntity<@NonNull List<ShortUrlResponse>> getShortUrlsByUserId(HttpServletRequest request) {
        List<ShortUrlResponse> urlsByUserId = shortUrlService.getUserUrls(request);
        return ResponseEntity.ok(urlsByUserId);
    }
}
