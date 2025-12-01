package com.example.expense.service;

import com.example.expense.entity.Attachment;
import com.example.expense.entity.Category;
import com.example.expense.entity.Income;
import com.example.expense.entity.User;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.repository.AttachmentRepository;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.repository.IncomeRepository;
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
public class IncomeService {

  private final IncomeRepository incomeRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final FileStorageService fileStorageService;
  private final AttachmentRepository attachmentRepository;
  private final GoalService goalService;

  public Page<Income> listIncomes(String keyword, Pageable pageable) {
    if (keyword == null || keyword.isBlank()) {
      return incomeRepository.findAll(pageable);
    }
    return incomeRepository.findByTitleContainingIgnoreCase(keyword, pageable);
  }

  public Optional<Income> findById(Long id) {
    return incomeRepository.findById(id);
  }

  public Page<Income> listIncomesByUser(Long userId, Pageable pageable) {
    return incomeRepository.findByUserId(userId, pageable);
  }

  public Income saveIncome(Income income, List<MultipartFile> files) {
    if (income == null) {
      throw new IllegalArgumentException("income.income_cannot_null");
    }
    if (income.getUser() == null || income.getUser().getId() == null) {
      throw new IllegalArgumentException("income.user_cannot_null");
    }

    User user =
        userRepository
            .findById(income.getUser().getId())
            .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
    income.setUser(user);

    if (income.getCategory() != null && income.getCategory().getId() != null) {
      Category category =
          categoryRepository
              .findById(income.getCategory().getId())
              .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
      income.setCategory(category);
    } else {
      income.setCategory(null);
    }

    if (income.getUuid() == null || income.getUuid().isEmpty()) {
      income.setUuid(UUID.randomUUID().toString());
    }

    if (income.getIncomeDate() == null) {
      income.setIncomeDate(LocalDate.now());
    }
    income.setCreatedAt(LocalDateTime.now());
    income.setUpdatedAt(LocalDateTime.now());

    Income savedIncome = incomeRepository.save(income);

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
                  .attachmentableId(savedIncome.getId())
                  .attachmentableType("Income")
                  .uploadedAt(LocalDateTime.now())
                  .build();

          attachmentRepository.save(attachment);
        }
      }
    }
    goalService.recalcCurrentAmountByUser(user.getId());

    return savedIncome;
  }

  @Transactional
  public Income updateIncome(Income income, List<MultipartFile> files) {
    if (income.getId() == null) {
      throw new IllegalArgumentException("income.invalid_id");
    }

    Income existing =
        incomeRepository
            .findById(income.getId())
            .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));

    existing.setTitle(income.getTitle());
    existing.setAmount(income.getAmount());
    existing.setIncomeDate(income.getIncomeDate());
    existing.setNote(income.getNote());

    if (income.getUser() != null && income.getUser().getId() != null) {
      User user =
          userRepository
              .findById(income.getUser().getId())
              .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
      existing.setUser(user);
    }

    if (income.getCategory() != null && income.getCategory().getId() != null) {
      Category category =
          categoryRepository
              .findById(income.getCategory().getId())
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
                  .attachmentableType("Income")
                  .uploadedAt(LocalDateTime.now())
                  .build();

          attachmentRepository.save(attachment);
        }
      }
    }
    existing.setUpdatedAt(LocalDateTime.now());
    goalService.recalcCurrentAmountByUser(existing.getUser().getId());

    return incomeRepository.save(existing);
  }

  @Transactional
  public boolean deleteById(Long id) {
    Income income =
        incomeRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
    Long userId = income.getUser().getId();
    goalService.recalcCurrentAmountByUser(userId);

    return incomeRepository
        .findById(id)
        .map(
            i -> {
              List<Attachment> attachments = i.getAttachments();

              if (attachments != null) {
                for (Attachment attachment : attachments) {
                  fileStorageService.deleteFile(attachment.getFileName());
                  attachmentRepository.delete(attachment);
                }
              }

              incomeRepository.delete(i);

              return true;
            })
        .orElse(false);
  }

  public List<Income> findAll() {
    return incomeRepository.findAll();
  }
}
