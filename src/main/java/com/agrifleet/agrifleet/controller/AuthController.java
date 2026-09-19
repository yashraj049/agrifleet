package com.agrifleet.agrifleet.controller;

import com.agrifleet.agrifleet.dto.LoginRequest;
import com.agrifleet.agrifleet.dto.LoginResponse;
import com.agrifleet.agrifleet.dto.RegisterRequest;
import com.agrifleet.agrifleet.model.User;
import com.agrifleet.agrifleet.security.JwtUtil;
import com.agrifleet.agrifleet.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new java.util.HashMap<>() {{
                        put("message",
                                "User registered successfully!");
                        put("userId", user.getId());
                        put("name", user.getName());
                        put("role", user.getRole());
                    }});
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new java.util.HashMap<>() {{
                        put("error", e.getMessage());
                    }});
        }
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {
        try {
            User user = authService
                    .findByEmail(request.getEmail());

            if (!authService.validatePassword(
                    request.getPassword(),
                    user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new java.util.HashMap<>() {{
                            put("error", "Invalid password!");
                        }});
            }

            String token = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getId(),
                    user.getRole().name());

            LoginResponse response = new LoginResponse(
                    token,
                    "Bearer",
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new java.util.HashMap<>() {{
                        put("error", e.getMessage());
                    }});
        }
    }
}