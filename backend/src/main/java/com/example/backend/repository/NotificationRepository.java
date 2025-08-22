package com.example.backend.repository;

import com.example.backend.entity.Notification;
import com.example.backend.constant.enums.NotificationStatus;
import com.example.backend.constant.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status);
    List<Notification> findByType(NotificationType type);
}
