package com.example.ms_auth.controller;

import com.example.ms_auth.dto.ApiResponse;
import com.example.ms_auth.dto.AuthRequest;
import com.example.ms_auth.dto.AuthResponse;
import com.example.ms_auth.dto.RegisterRequest;
import com.example.ms_auth.dto.UserResponse;
import com.example.ms_auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message("Usuario registrado correctamente")
                        .data(authService.register(request))
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Login exitoso")
                        .data(authService.login(request))
                        .build()
        );
    }
}
