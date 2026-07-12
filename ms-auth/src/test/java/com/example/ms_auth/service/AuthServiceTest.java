package com.example.ms_auth.service;

import com.example.ms_auth.dto.AuthRequest;
import com.example.ms_auth.dto.RegisterRequest;
import com.example.ms_auth.model.Role;
import com.example.ms_auth.model.User;
import com.example.ms_auth.repository.RoleRepository;
import com.example.ms_auth.repository.UserRepository;
import com.example.ms_auth.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCreatesActiveUserWithEncodedPassword() {
        RegisterRequest request = new RegisterRequest("admin", "secret", "admin@market.cl", 1L);
        Role role = new Role(1L, "ADMIN");
        User saved = new User(10L, "admin", "encoded", "admin@market.cl", role, true);

        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        var response = authService.register(request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getRole()).isEqualTo("ADMIN");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void loginRejectsInvalidPassword() {
        Role role = new Role(1L, "USER");
        User user = new User(1L, "user", "encoded", "user@market.cl", role, true);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("bad", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new AuthRequest("user", "bad")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Credenciales");
    }

    @Test
    void loginReturnsJwtForActiveUser() {
        Role role = new Role(1L, "USER");
        User user = new User(1L, "user", "encoded", "user@market.cl", role, true);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);
        when(jwtProvider.generarToken("user", "USER")).thenReturn("jwt-token");

        var response = authService.login(new AuthRequest("user", "secret"));

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUser().getUsername()).isEqualTo("user");
    }
}
