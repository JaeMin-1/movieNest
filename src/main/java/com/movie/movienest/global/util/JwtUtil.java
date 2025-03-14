package com.movie.movienest.global.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "eW91cl9iYXNlNjRfZW5jb2RlZF8yNTZiaXRfc2VjcmV0X2tleQ=="; // 256-bit 키 필요
    private static final long EXPIRATION_TIME = 86400000; // 1일 (밀리초 단위)
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000; // 7일 (밀리초 단위)

    private final Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

    // Access Token 생성 (짧은 유효기간)
    public String generateAccessToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // Refresh Token 생성 (긴 유효기간)
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    // JWT 토큰 검증 및 파싱
    public String validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return claimsJws.getPayload().getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    // refreshToken이 유효한 경우 새로운 accessToken 발급
    public String validateRefreshToken(String refreshToken) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(refreshToken);

            String email = claimsJws.getPayload().getSubject();
            return generateAccessToken(email); // 새로운 accessToken 발급
        } catch (Exception e) {
            return null;
        }
    }
}
