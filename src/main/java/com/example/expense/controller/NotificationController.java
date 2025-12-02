package com.example.expense.controller;

import com.example.expense.dto.NotificationResponse;
import com.example.expense.entity.Notification;
import com.example.expense.entity.User;
import com.example.expense.service.NotificationService;
import com.example.expense.service.UserService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;
  private final UserService userService;

  private NotificationResponse mapToResponse(Notification n) {
    return new NotificationResponse(
        n.getId(),
        n.getUuid(),
        n.getMessage(),
        n.getNotificationType() != null ? n.getNotificationType().name() : "UNKNOWN",
        n.getSourceEntity() != null ? n.getSourceEntity().name() : "UNKNOWN",
        n.getSourceId(),
        n.getIsRead());
  }

  @GetMapping
  public List<NotificationResponse> list(
      Authentication auth,
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

    User user = userService.getByEmail(auth.getName());

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    Page<Notification> notifications = notificationService.findByUser(user, pageable);

    return notifications.stream().map(this::mapToResponse).collect(Collectors.toList());
  }

  @GetMapping("/unread")
  public List<NotificationResponse> listUnread(Authentication auth) {
    User user = userService.getByEmail(auth.getName());
    List<Notification> unread = notificationService.findUnreadByUser(user);
    return unread.stream().map(this::mapToResponse).collect(Collectors.toList());
  }

  @PostMapping("/{id}/read")
  public NotificationResponse markAsRead(@PathVariable Long id, Authentication auth) {
    User user = userService.getByEmail(auth.getName());
    Notification notification = notificationService.markAsRead(user, id);
    return mapToResponse(notification);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id, Authentication auth) {
    User user = userService.getByEmail(auth.getName());
    notificationService.delete(user, id);
  }
}
