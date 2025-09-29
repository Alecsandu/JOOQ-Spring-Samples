package com.dockerino.demo.api;

import com.dockerino.demo.model.dtos.ShortUrlRequest;
import com.dockerino.demo.model.dtos.ShortUrlResponse;
import com.dockerino.demo.service.ShortUrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<ShortUrlResponse> createShortUrl(@Valid @RequestBody ShortUrlRequest shortenRequest, HttpServletRequest request) {
        ShortUrlResponse response = shortUrlService.createShortUrl(shortenRequest.getOriginalUrl(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> getOriginalUrlByShortCode(@PathVariable String shortCode) {
        return shortUrlService.getOriginalUrl(shortCode)
                .<ResponseEntity<Void>>map(originalUrl -> ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(originalUrl))
                        .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                        .header(HttpHeaders.PRAGMA, "no-cache")
                        .header(HttpHeaders.EXPIRES, "0")
                        .build())
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<ShortUrlResponse>> getShortUrlsByUserId(HttpServletRequest request) {
        List<ShortUrlResponse> urlsByUserId = shortUrlService.getUserUrls(request);
        return ResponseEntity.ok(urlsByUserId);
    }
}