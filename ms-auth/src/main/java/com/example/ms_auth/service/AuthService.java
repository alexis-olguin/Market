package com.example.ms_auth.service;

import com.example.ms_auth.dto.*;
import com.example.ms_auth.model.Role;
import com.example.ms_auth.model.User;
import com.example.ms_auth.repository.RoleRepository;
import com.example.ms_auth.repository.UserRepository;
import com.example.ms_auth.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username ya existe");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(role);
        user.setActive(true);

        user = userRepository.save(user);
        log.info("Usuario registrado: {}", user.getUsername());

        return mapToUserResponse(user);
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        if (!user.getActive()) {
            throw new IllegalArgumentException("Usuario inactivo");
        }

        String token = jwtProvider.generarToken(user.getUsername(), user.getRole().getName());
        log.info("Login exitoso para usuario: {}", user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .user(mapToUserResponse(user))
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .active(user.getActive())
                .build();
    }
}
