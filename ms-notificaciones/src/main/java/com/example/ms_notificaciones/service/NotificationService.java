package com.example.ms_notificaciones.service;

import com.example.ms_notificaciones.dto.NotificationRequest;
import com.example.ms_notificaciones.dto.NotificationResponse;
import com.example.ms_notificaciones.model.Notification;
import com.example.ms_notificaciones.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationResponse crearNotificacion(NotificationRequest request) {
        log.info("Creando notificación de tipo: {}", request.getType());

        Notification notif = new Notification();
        notif.setType(request.getType());
        notif.setMessage(request.getMessage());
        notif.setStatus("SENT");
        notif.setCreatedAt(LocalDateTime.now());

        notif = repository.save(notif);

        log.info("Notificación guardada en base de datos con ID {}", notif.getId());

        return mapToResponse(notif);
    }

    public List<NotificationResponse> listarNotificaciones() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public NotificationResponse obtenerNotificacionPorId(Long id) {
        Notification notif = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada"));
        return mapToResponse(notif);
    }

    private NotificationResponse mapToResponse(Notification notif) {
        return NotificationResponse.builder()
                .id(notif.getId())
                .type(notif.getType())
                .message(notif.getMessage())
                .status(notif.getStatus())
                .createdAt(notif.getCreatedAt())
                .build();
    }
}
