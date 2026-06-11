package com.dudus.diecast_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dudus.diecast_api.model.Users;
import com.dudus.diecast_api.repository.UsersRepository;
import com.dudus.diecast_api.security.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;


@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsersRepository usersRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder){
        this.usersRepository = usersRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body){
        String username = body.get("username");
        String password = body.get("password");

        Users user = usersRepository.findByUsername(username);

        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Usernam atau Password salah"));
        }
        String token =jwtUtil.generateToken(username);
        return ResponseEntity.ok(Map.of("token", token));
    }
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> body){
        String username = body.get("username");
        String password = body.get("password");

        if (usersRepository.findByUsername(username) != null) {
            return ResponseEntity.status(400)
                        .body(Map.of("message", "Usernam sudah dipakai"));
        }

        Users user = new Users();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        usersRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Register Berhasil"));
    }
}
