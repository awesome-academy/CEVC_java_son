package com.example.expense.service;

import com.example.expense.entity.Budget;
import com.example.expense.entity.User;
import com.example.expense.enums.NotificationType;
import com.example.expense.enums.SourceEntity;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.repository.BudgetRepository;
import com.example.expense.repository.UserRepository;
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
public class BudgetService {

  private final BudgetRepository budgetRepository;
  private final UserRepository userRepository;
  private final NotificationService notificationService;

  public Page<Budget> listBudgets(Pageable pageable, User user) {
    return budgetRepository.findByUser(user, pageable);
  }

  public Budget saveBudget(Budget budget) {
    if (budget.getUuid() == null) {
      budget.setUuid(UUID.randomUUID().toString());
    }

    Budget savedBudget = budgetRepository.save(budget);

    notificationService.createNotification(
        savedBudget.getUser(),
        NotificationType.INFO,
        SourceEntity.BUDGET,
        savedBudget.getId(),
        "New budget created with limit: " + savedBudget.getAmountLimit());

    return savedBudget;
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

    Budget savedBudget = budgetRepository.save(existing);

    notificationService.createNotification(
        savedBudget.getUser(),
        NotificationType.INFO,
        SourceEntity.BUDGET,
        savedBudget.getId(),
        "Budget updated with new limit: " + savedBudget.getAmountLimit());

    return savedBudget;
  }

  public void delete(Long id) {
    Budget budget =
        budgetRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
    budgetRepository.delete(budget);

    notificationService.createNotification(
        budget.getUser(),
        NotificationType.INFO,
        SourceEntity.BUDGET,
        budget.getId(),
        "Budget deleted with limit: " + budget.getAmountLimit());
  }

  public Optional<Budget> findById(Long id) {
    return budgetRepository.findById(id);
  }
}
