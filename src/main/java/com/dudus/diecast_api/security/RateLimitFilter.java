package com.dudus.diecast_api.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitFilter extends OncePerRequestFilter{
    //bucket per IP Adress

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket(){
    Bandwidth limit = Bandwidth.builder()
            .capacity(10)
            .refillGreedy(10, Duration.ofMinutes(1))
            .build();
    return Bucket.builder().addLimit(limit).build();
}

    private Bucket getBucket(String ip){
        return buckets.computeIfAbsent(ip, k -> createNewBucket());
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
                //Ratelimit endpoint
                String path = request.getRequestURI();  
                if (!path.startsWith("/api/auth")) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String ip = request.getRemoteAddr();
                Bucket bucket = getBucket(ip);

                if (bucket.tryConsume(1)) {
                    filterChain.doFilter(request, response);
                }else{
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.setContentType("application/json");
                    response.getWriter().write("""
                {
                    "status": 429,
                    "message": "Terlalu banyak request. Coba lagi dalam 1 menit."
                }
                """);
                }

    }
}
