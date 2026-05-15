package com.example.ms_auth.controller;

import com.example.ms_auth.dto.ApiResponse;
import com.example.ms_auth.dto.RoleResponse;
import com.example.ms_auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        return ResponseEntity.ok(
                ApiResponse.<List<RoleResponse>>builder()
                        .success(true)
                        .data(userService.getAllRoles())
                        .build()
        );
    }
}
