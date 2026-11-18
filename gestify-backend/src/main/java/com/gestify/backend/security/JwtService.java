package com.gestify.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {

    private static final String BUSINESS_CLAIM = "id_negocio";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(Long businessId, String email) {
        return Jwts.builder()
                .claims(Map.of(BUSINESS_CLAIM, businessId))
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    /** Devuelve el usuario del token, o vacío si el token es inválido, está vencido o no trae negocio. */
    public Optional<AuthUser> parseAuthUser(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Object businessId = claims.get(BUSINESS_CLAIM);
            if (!(businessId instanceof Number number)) {
                return Optional.empty();
            }
            return Optional.of(new AuthUser(number.longValue(), claims.getSubject()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}