package com.example.ms_notificaciones.service;

import com.example.ms_notificaciones.dto.NotificationRequest;
import com.example.ms_notificaciones.model.Notification;
import com.example.ms_notificaciones.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationService service;

    @Test
    void crearNotificacionStoresSentStatus() {
        Notification saved = new Notification(1L, "CRITICAL_STOCK", "Stock bajo", "SENT", LocalDateTime.now());
        when(repository.save(any(Notification.class))).thenReturn(saved);

        var response = service.crearNotificacion(new NotificationRequest("CRITICAL_STOCK", "Stock bajo"));

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo("SENT");
    }

    @Test
    void listarNotificacionesMapsRepositoryResults() {
        when(repository.findAll()).thenReturn(List.of(
                new Notification(1L, "SYSTEM", "Servicio iniciado", "SENT", LocalDateTime.now())));

        var response = service.listarNotificaciones();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getType()).isEqualTo("SYSTEM");
    }

    @Test
    void obtenerNotificacionPorIdFailsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerNotificacionPorId(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no encontrada");
    }
}
