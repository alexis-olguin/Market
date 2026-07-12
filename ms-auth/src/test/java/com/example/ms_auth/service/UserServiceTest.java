package com.example.ms_auth.service;

import com.example.ms_auth.model.Role;
import com.example.ms_auth.model.User;
import com.example.ms_auth.repository.RoleRepository;
import com.example.ms_auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService service;

    @Test
    void getAllUsersMapsRolesAndActiveStatus() {
        Role role = new Role(1L, "ADMIN");
        when(userRepository.findAll()).thenReturn(List.of(
                new User(10L, "admin", "encoded", "admin@market.cl", role, true)
        ));

        var response = service.getAllUsers();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getRole()).isEqualTo("ADMIN");
        assertThat(response.get(0).getActive()).isTrue();
    }

    @Test
    void getAllRolesMapsRepositoryResults() {
        when(roleRepository.findAll()).thenReturn(List.of(new Role(1L, "USER")));

        var response = service.getAllRoles();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("USER");
    }
}
