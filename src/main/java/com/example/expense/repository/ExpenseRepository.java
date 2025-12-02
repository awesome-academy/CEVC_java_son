package com.example.expense.repository;

import com.example.expense.entity.Expense;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
  Page<Expense> findByTitleContainingIgnoreCase(String title, Pageable pageable);

  List<Expense> findByUserId(Long userId);

  @Query(
      "SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.expenseDate BETWEEN :start AND :end")
  Optional<BigDecimal> sumByUserAndDateBetween(
      @Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

  @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.expenseDate BETWEEN :start AND :end")
  Optional<BigDecimal> sumByDateBetween(
      @Param("start") LocalDate start, @Param("end") LocalDate end);
}
