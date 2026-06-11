package com.dudus.diecast_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET = "diecastdata-secret-key-yang-panjang-banget-biar-aman-256bit";
    private static final long EXPIRATION = 1000 * 60 * 60 * 24; //24 jam

    private Key getKey(){
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    //Generate token username
    public String generateToken(String username, String role){
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // username dari token
    public String exctractUsername(String token){
            return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    //role 
    public String extractRole(String token){
        return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);
    }

    // Validasi token
    public Boolean isTokenValid(String token){
        try {
            Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
       
    }
}
