package com.dudus.diecast_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity

public class SecurityConfig {
    private final JwtUtil jwtUtil;
    
    public SecurityConfig(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
}
// inject manual limit
    @Bean
    public RateLimitFilter rateLimitFilter() {
        return new RateLimitFilter();
    }
    @Bean
    public JwtFilter jwtFilter() {
    return new JwtFilter(jwtUtil); // inject JwtUtil
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .csrf(csrf -> csrf.disable())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/swagger-ui/**",
                                  "/swagger-ui.html",
                                  "/v3/api-docs/**").permitAll()         
                // EndPoinKhusus Owner
                .requestMatchers(HttpMethod.GET,"/api/arus-kas/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.POST,"/api/arus-kas/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.DELETE,"/api/diecast/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.PUT,"/api/diecast/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.POST,"/api/transaksi/batal/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.POST,"/api/booking/lunas/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.GET, "/api/laporan/**").hasRole("OWNER")

                // EndPoint Reseller
                .requestMatchers(HttpMethod.GET, "/api/diecast/reseller").hasRole("RESELLER")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException)-> 
                    response.sendError(401, "Unauthorized"))
                
            )
            .addFilterBefore(rateLimitFilter(), UsernamePasswordAuthenticationFilter.class) 
            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception{
                return config.getAuthenticationManager();
            }

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> {
            throw new UsernameNotFoundException("Not Used");
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        return source -> {
            UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
            s.registerCorsConfiguration("/**", config);
            return s.getCorsConfiguration(source);
        };
    }
}
