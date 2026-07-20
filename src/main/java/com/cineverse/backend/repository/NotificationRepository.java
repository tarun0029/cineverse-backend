package com.cineverse.backend.repository;

import com.cineverse.backend.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    long countByUserIdAndReadFalse(Integer userId);
}
