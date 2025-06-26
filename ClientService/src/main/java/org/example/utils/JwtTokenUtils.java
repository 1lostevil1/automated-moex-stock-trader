package org.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration lifetime;

    /**
     * Составит неудобства для дальнейшего развития api,
     * когда надо будет перевыпускать токен при любои изменении данных пользователя
     */
    @Deprecated
    public String generateToken(UserDetails userDetails, Map<String, Object> addingClaims) {

        Map<String,Object> claims = new HashMap<>();

        claims.put("username", userDetails.getUsername());
        claims.putAll(addingClaims);

        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + lifetime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)),SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDetails userDetails) {

        Map<String,Object> claims = new HashMap<>();

        claims.put("username", userDetails.getUsername());

        Date issuedAt = new Date();
        Date expiredAt  = new Date(issuedAt.getTime() + lifetime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)),SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsername(String token) {
        var bearerStarting = "Bearer ";
        return  getClaimsFromToken(token.substring(bearerStarting.length())).getSubject();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
