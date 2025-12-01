package com.example.expense.repository;

import com.example.expense.entity.Goal;
import com.example.expense.enums.GoalType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
  List<Goal> findByUserId(Long userId);

  Optional<Goal> findByUserIdAndGoalTypeAndPeriod(Long userId, GoalType goalType, LocalDate period);
}
