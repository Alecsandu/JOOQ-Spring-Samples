package com.dockerino.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final Integer jwtExpirationMs;
    private final KeyPair keyChain;
    private static final String SIG_ALGORITHM = "Ed25519";

    public JwtTokenProvider(
            @Value("${app.jwt.expiration-ms}") Integer jwtExpirationMs,
            @Value("${app.jwt.public-key-b64}") String publicKeyB64,
            @Value("${app.jwt.private-key-b64}") String privateKeyB64
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.jwtExpirationMs = jwtExpirationMs;

        KeyFactory keyFactory = KeyFactory.getInstance(SIG_ALGORITHM);
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyB64);
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyB64);

        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        keyChain = new KeyPair(publicKey, privateKey);
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        UUID userId = ((CustomUserDetails) userPrincipal).getId();

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(keyChain.getPrivate(), Jwts.SIG.EdDSA)
                .compact();
    }

    public UUID getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser().verifyWith(keyChain.getPublic()).build().parseSignedClaims(token).getPayload();
        return UUID.fromString(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(keyChain.getPublic()).build().parseSignedClaims(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}