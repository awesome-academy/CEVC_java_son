package com.example.expense.service;

import com.example.expense.entity.Goal;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.repository.ExpenseRepository;
import com.example.expense.repository.GoalRepository;
import com.example.expense.repository.IncomeRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoalService {

  private final GoalRepository goalRepository;
  private final ExpenseRepository expenseRepository;
  private final IncomeRepository incomeRepository;

  @Transactional
  public Goal create(Goal goal) {
    goal.setUuid(UUID.randomUUID().toString());
    goal.setCurrentAmount(BigDecimal.ZERO);
    goal.setAchieved(false);
    return goalRepository.save(goal);
  }

  public List<Goal> findByUserId(Long userId) {
    return goalRepository.findByUserId(userId);
  }

  public Optional<Goal> findById(Long id) {
    return goalRepository.findById(id);
  }

  public Goal getOrThrow(Long id) {
    return goalRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
  }

  @Transactional
  public Goal update(Goal updatedGoal) {
    Goal existing = getOrThrow(updatedGoal.getId());

    existing.setName(updatedGoal.getName());
    existing.setGoalType(updatedGoal.getGoalType());
    existing.setTargetAmount(updatedGoal.getTargetAmount());
    existing.setPeriod(updatedGoal.getPeriod());
    existing.setDeadline(updatedGoal.getDeadline());

    Goal saved = goalRepository.save(existing);

    return recalcCurrentAmount(saved);
  }

  @Transactional
  public void delete(Long id) {
    Goal existing = getOrThrow(id);
    goalRepository.delete(existing);
  }

  @Transactional
  public Goal recalcCurrentAmountByUser(Long userId) {
    List<Goal> goals = goalRepository.findByUserId(userId);
    Goal lastUpdatedGoal = null;
    for (Goal goal : goals) {
      lastUpdatedGoal = recalcCurrentAmount(goal);
    }
    return lastUpdatedGoal;
  }

  @Transactional
  public Goal recalcCurrentAmount(Goal goal) {

    BigDecimal current = BigDecimal.ZERO;

    LocalDate now = LocalDate.now();
    LocalDate start = now.withDayOfMonth(1);
    LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

    switch (goal.getGoalType()) {
      case SAVING -> {
        current =
            incomeRepository
                .sumByUserAndDateBetween(goal.getUser().getId(), start, end)
                .orElse(BigDecimal.ZERO);
      }

      case SPENDING, EXPENSE_LIMIT -> {
        current =
            expenseRepository
                .sumByUserAndDateBetween(goal.getUser().getId(), start, end)
                .orElse(BigDecimal.ZERO);
      }
    }

    goal.setCurrentAmount(current);
    goal.setAchieved(current.compareTo(goal.getTargetAmount()) >= 0);

    return goalRepository.save(goal);
  }
}
