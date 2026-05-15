package com.example.ms_notificaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.ms_notificaciones.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
