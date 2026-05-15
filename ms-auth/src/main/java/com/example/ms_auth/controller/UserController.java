package com.example.ms_auth.controller;

import com.example.ms_auth.dto.ApiResponse;
import com.example.ms_auth.dto.UserResponse;
import com.example.ms_auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(
                ApiResponse.<List<UserResponse>>builder()
                        .success(true)
                        .data(userService.getAllUsers())
                        .build()
        );
    }
}
