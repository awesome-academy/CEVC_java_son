package com.example.expense.repository;

import com.example.expense.entity.Notification;
import com.example.expense.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  Page<Notification> findByUser(User user, Pageable pageable);

  List<Notification> findByUserAndIsReadFalse(User user);
}
