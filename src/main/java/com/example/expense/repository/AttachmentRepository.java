package com.example.expense.repository;

import com.example.expense.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
  // do something if needed
}
