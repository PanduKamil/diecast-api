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

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    
    public SecurityConfig(JwtFilter jwtFilter){
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/error").permitAll()
                // EndPoinKhusus Owner
                .requestMatchers(HttpMethod.GET,"/api/arus-kas/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.POST,"/api/arus-kas/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.DELETE,"/api/diecast/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.PUT,"/api/diecast/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.POST,"/api/transaksi/batal/**").hasRole("OWNER")
                .requestMatchers(HttpMethod.POST,"/api/booking/lunas/**").hasRole("OWNER")

                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException)-> 
                    response.sendError(401, "Unauthorized"))
                
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
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
}
