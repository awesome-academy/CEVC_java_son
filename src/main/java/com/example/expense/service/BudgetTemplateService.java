package com.example.expense.service;

import com.example.expense.entity.BudgetTemplate;
import com.example.expense.entity.User;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.repository.BudgetTemplateRepository;
import com.example.expense.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetTemplateService {

  private final BudgetTemplateRepository budgetTemplateRepository;
  private final UserRepository userRepository;

  public Page<BudgetTemplate> listTemplates(String keyword, Pageable pageable) {
    if (keyword == null || keyword.isBlank()) {
      return budgetTemplateRepository.findAll(pageable);
    }
    return budgetTemplateRepository.findByNameContainingIgnoreCase(keyword, pageable);
  }

  public Optional<BudgetTemplate> findById(Long id) {
    return budgetTemplateRepository.findById(id);
  }

  public BudgetTemplate saveTemplate(BudgetTemplate template) {
    if (template == null) {
      throw new IllegalArgumentException("error.template_cannot_be_null");
    }

    if (template.getCreatedBy() != null && template.getCreatedBy().getId() != null) {
      User user =
          userRepository
              .findById(template.getCreatedBy().getId())
              .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
      template.setCreatedBy(user);
    }

    if (template.getUuid() == null || template.getUuid().isEmpty()) {
      template.setUuid(UUID.randomUUID().toString());
    }
    template.setCreatedAt(LocalDateTime.now());
    template.setUpdatedAt(LocalDateTime.now());

    return budgetTemplateRepository.save(template);
  }

  public BudgetTemplate updateTemplate(BudgetTemplate template) {
    if (template.getId() == null) {
      throw new ResourceNotFoundException("template.invalid_id");
    }

    BudgetTemplate existing =
        budgetTemplateRepository
            .findById(template.getId())
            .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));

    existing.setName(template.getName());
    existing.setPeriod(template.getPeriod());
    existing.setDefaultAmount(template.getDefaultAmount());
    existing.setCategory(template.getCategory());

    if (template.getUpdatedBy() != null && template.getUpdatedBy().getId() != null) {
      User user =
          userRepository
              .findById(template.getUpdatedBy().getId())
              .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
      existing.setUpdatedBy(user);
    }
    existing.setUpdatedAt(LocalDateTime.now());

    return budgetTemplateRepository.save(existing);
  }

  public boolean deleteById(Long id) {
    return budgetTemplateRepository
        .findById(id)
        .map(
            template -> {
              budgetTemplateRepository.delete(template);
              return true;
            })
        .orElse(false);
  }

  public List<BudgetTemplate> findAll() {
    return budgetTemplateRepository.findAll();
  }
}
