package com.dockerino.demo.api;

import com.dockerino.demo.config.properties.JwtProperties;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/well-known")
public class WellKnownApi {

    private final RSAKey rsaKey;

    private final JwtProperties jwtProperties;

    public WellKnownApi(RSAKey rsaKey, JwtProperties jwtProperties) {
        this.rsaKey = rsaKey;
        this.jwtProperties = jwtProperties;
    }

    @GetMapping("/jwks")
    ResponseEntity<WellKnownResponse> getJwks() {
        List<String> jwks = List.of(rsaKey.toPublicJWK().toString());
        return ResponseEntity.ok(new WellKnownResponse(jwks));
    }

    @GetMapping("/public-key")
    ResponseEntity<String> getPublicKey() {
        return ResponseEntity.ok("-----BEGIN PUBLIC KEY-----\n" +
                jwtProperties.publicKey() +
                "\n-----END PUBLIC KEY-----");
    }

    private record WellKnownResponse(List<String> jwks){}

}
