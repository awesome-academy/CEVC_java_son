package com.example.expense.service;

import com.example.expense.entity.Goal;
import com.example.expense.enums.NotificationType;
import com.example.expense.enums.SourceEntity;
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
  private final NotificationService notificationService;

  @Transactional
  public Goal create(Goal goal) {
    goal.setUuid(UUID.randomUUID().toString());
    goal.setCurrentAmount(BigDecimal.ZERO);
    goal.setAchieved(false);

    Goal savedGoal = goalRepository.save(goal);

    notificationService.createNotification(
        savedGoal.getUser(),
        NotificationType.INFO,
        SourceEntity.GOAL,
        savedGoal.getId(),
        "New goal created: " + savedGoal.getName() + " with target " + savedGoal.getTargetAmount());

    return savedGoal;
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

    Goal savedGoal = goalRepository.save(existing);

    notificationService.createNotification(
        savedGoal.getUser(),
        NotificationType.INFO,
        SourceEntity.GOAL,
        savedGoal.getId(),
        "Goal updated: " + savedGoal.getName() + " with new target " + savedGoal.getTargetAmount());

    return recalcCurrentAmount(savedGoal);
  }

  @Transactional
  public void delete(Long id) {
    Goal existing = getOrThrow(id);
    goalRepository.delete(existing);

    notificationService.createNotification(
        existing.getUser(),
        NotificationType.INFO,
        SourceEntity.GOAL,
        existing.getId(),
        "Goal deleted: " + existing.getName());
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

    boolean previouslyAchieved = goal.getAchieved();
    goal.setCurrentAmount(current);
    goal.setAchieved(current.compareTo(goal.getTargetAmount()) >= 0);

    Goal savedGoal = goalRepository.save(goal);

    // Nếu vừa đạt mục tiêu, gửi notification
    if (!previouslyAchieved && goal.getAchieved()) {
      notificationService.createNotification(
          savedGoal.getUser(),
          NotificationType.GOAL_REACHED,
          SourceEntity.GOAL,
          savedGoal.getId(),
          "Congratulations! Goal reached: " + savedGoal.getName());
    }

    return savedGoal;
  }
}
