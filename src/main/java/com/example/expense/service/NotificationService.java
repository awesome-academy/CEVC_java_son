package com.example.expense.service;

import com.example.expense.entity.Notification;
import com.example.expense.entity.User;
import com.example.expense.enums.NotificationType;
import com.example.expense.enums.SourceEntity;
import com.example.expense.repository.NotificationRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;

  public Notification createNotification(
      User user, NotificationType type, SourceEntity source, Long sourceId, String message) {
    Notification notification =
        Notification.builder()
            .uuid(UUID.randomUUID().toString())
            .user(user)
            .notificationType(type)
            .sourceEntity(source)
            .sourceId(sourceId)
            .message(message)
            .isRead(false)
            .build();
    return notificationRepository.save(notification);
  }
}
