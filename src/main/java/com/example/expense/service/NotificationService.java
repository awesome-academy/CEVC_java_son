package com.example.expense.service;

import com.example.expense.entity.Notification;
import com.example.expense.entity.User;
import com.example.expense.enums.NotificationType;
import com.example.expense.enums.SourceEntity;
import com.example.expense.exception.AccessDeniedException;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;

  public Notification createNotification(
      User user, NotificationType type, SourceEntity source, Long sourceId, String message) {

    if (message == null || message.trim().isEmpty()) {
      throw new IllegalArgumentException("notification_message_empty");
    }
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

  @Transactional(readOnly = true)
  public Page<Notification> findByUser(User user, Pageable pageable) {
    return notificationRepository.findByUser(user, pageable);
  }

  @Transactional(readOnly = true)
  public List<Notification> findUnreadByUser(User user) {
    return notificationRepository.findByUserAndIsReadFalse(user);
  }

  public Notification markAsRead(User user, Long id) {
    Notification notification =
        notificationRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("error_model.not_found"));
    if (!notification.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("Not allowed to mark this notification");
    }
    notification.setIsRead(true);
    return notificationRepository.save(notification);
  }

  public void delete(User user, Long id) {
    Notification notification =
        notificationRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("error_model.not_found"));
    if (!notification.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("Not allowed to delete this notification");
    }
    notificationRepository.delete(notification);
  }
}
