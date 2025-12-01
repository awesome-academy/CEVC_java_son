package com.example.expense.repository;

import com.example.expense.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
  // do something if needed
}
