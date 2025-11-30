package com.example.expense.service;

import com.example.expense.entity.Budget;
import com.example.expense.entity.User;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.repository.BudgetRepository;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BudgetService {

  private final BudgetRepository budgetRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;

  public Page<Budget> listBudgets(Pageable pageable, User user) {
    return budgetRepository.findByUser(user, pageable);
  }

  public Budget saveBudget(Budget budget) {

    if (budget.getUuid() == null) {
      budget.setUuid(UUID.randomUUID().toString());
    }

    budget.setCreatedAt(LocalDateTime.now());
    budget.setUpdatedAt(LocalDateTime.now());

    return budgetRepository.save(budget);
  }

  public Budget updateBudget(Budget budget) {
    Budget existing =
        budgetRepository
            .findById(budget.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

    existing.setAmountLimit(budget.getAmountLimit());
    existing.setPeriod(budget.getPeriod());
    existing.setPeriodType(budget.getPeriodType());
    existing.setCategory(budget.getCategory());
    existing.setUpdatedAt(LocalDateTime.now());

    return budgetRepository.save(existing);
  }

  public void delete(Long id) {
    Budget budget =
        budgetRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
    budgetRepository.delete(budget);
  }

  public java.util.Optional<Budget> findById(Long id) {
    return budgetRepository.findById(id);
  }
}
