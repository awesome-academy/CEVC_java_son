package com.example.expense.service;

import com.example.expense.entity.Attachment;
import com.example.expense.entity.Category;
import com.example.expense.entity.Expense;
import com.example.expense.entity.User;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.repository.AttachmentRepository;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.repository.ExpenseRepository;
import com.example.expense.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final FileStorageService fileStorageService;
  private final AttachmentRepository attachmentRepository;

  public Page<Expense> listExpenses(String keyword, Pageable pageable) {
    if (keyword == null || keyword.isBlank()) {
      return expenseRepository.findAll(pageable);
    }
    return expenseRepository.findByTitleContainingIgnoreCase(keyword, pageable);
  }

  public Optional<Expense> findById(Long id) {
    return expenseRepository.findById(id);
  }

  public Expense saveExpense(Expense expense, List<MultipartFile> files) {
    if (expense == null) {
      throw new IllegalArgumentException("expense.expense_cannot_null");
    }
    if (expense.getUser() == null || expense.getUser().getId() == null) {
      throw new IllegalArgumentException("expense.user_cannot_null");
    }

    User user =
        userRepository
            .findById(expense.getUser().getId())
            .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
    expense.setUser(user);

    if (expense.getCategory() != null && expense.getCategory().getId() != null) {
      Category category =
          categoryRepository
              .findById(expense.getCategory().getId())
              .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
      expense.setCategory(category);
    } else {
      expense.setCategory(null);
    }

    if (expense.getUuid() == null || expense.getUuid().isEmpty()) {
      expense.setUuid(UUID.randomUUID().toString());
    }

    if (expense.getExpenseDate() == null) {
      expense.setExpenseDate(LocalDate.now());
    }
    expense.setCreatedAt(LocalDateTime.now());
    expense.setUpdatedAt(LocalDateTime.now());

    Expense savedExpense = expenseRepository.save(expense);

    // Handle uploading new attachments.
    if (files != null) {
      for (MultipartFile file : files) {
        if (!file.isEmpty()) {
          String fileName = fileStorageService.storeFile(file);

          Attachment attachment =
              Attachment.builder()
                  .uuid(UUID.randomUUID().toString())
                  .fileName(file.getOriginalFilename())
                  .filePath(fileName)
                  .fileType(file.getContentType())
                  .attachmentableId(savedExpense.getId())
                  .attachmentableType("Expense")
                  .uploadedAt(LocalDateTime.now())
                  .build();

          attachmentRepository.save(attachment);
        }
      }
    }

    return savedExpense;
  }

  public Expense updateExpense(Expense expense, List<MultipartFile> files) {
    if (expense.getId() == null) {
      throw new ResourceNotFoundException("expense.invalid_id");
    }

    Expense existing =
        expenseRepository
            .findById(expense.getId())
            .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));

    existing.setTitle(expense.getTitle());
    existing.setAmount(expense.getAmount());
    existing.setExpenseDate(expense.getExpenseDate());
    existing.setNote(expense.getNote());

    if (expense.getUser() != null && expense.getUser().getId() != null) {
      User user =
          userRepository
              .findById(expense.getUser().getId())
              .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
      existing.setUser(user);
    }

    if (expense.getCategory() != null && expense.getCategory().getId() != null) {
      Category category =
          categoryRepository
              .findById(expense.getCategory().getId())
              .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
      existing.setCategory(category);
    } else {
      existing.setCategory(null);
    }

    // Handle uploading new attachments.
    if (files != null) {
      for (MultipartFile file : files) {
        if (!file.isEmpty()) {
          String fileName = fileStorageService.storeFile(file);

          Attachment attachment =
              Attachment.builder()
                  .uuid(UUID.randomUUID().toString())
                  .fileName(file.getOriginalFilename())
                  .filePath(fileName)
                  .fileType(file.getContentType())
                  .attachmentableId(existing.getId())
                  .attachmentableType("Expense")
                  .uploadedAt(LocalDateTime.now())
                  .build();

          attachmentRepository.save(attachment);
        }
      }
    }

    return expenseRepository.save(existing);
  }

  public boolean deleteById(Long id) {
    return expenseRepository
        .findById(id)
        .map(
            expense -> {
              expenseRepository.delete(expense);
              return true;
            })
        .orElse(false);
  }

  public List<Expense> findAll() {
    return expenseRepository.findAll();
  }
}
