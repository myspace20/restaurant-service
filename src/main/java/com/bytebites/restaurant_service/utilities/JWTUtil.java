package com.bytebites.restaurant_service.utilities;

import com.bytebites.restaurant_service.exceptions.InvalidJWTTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessTokenExpiration;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new InvalidJWTTokenException("Expired JWT token");
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            throw new InvalidJWTTokenException("Invalid JWT token");
        }
    }

    public String generateAccessToken(Long id, List<String> roles) {
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}