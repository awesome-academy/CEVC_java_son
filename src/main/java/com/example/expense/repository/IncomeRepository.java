package com.example.expense.repository;

import com.example.expense.entity.Income;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IncomeRepository extends JpaRepository<Income, Long> {
  Page<Income> findByTitleContainingIgnoreCase(String title, Pageable pageable);

  Page<Income> findByUserId(Long userId, Pageable pageable);

  @Query(
      "SELECT SUM(i.amount) FROM Income i WHERE i.user.id = :userId AND i.incomeDate BETWEEN :start AND :end")
  Optional<BigDecimal> sumByUserAndDateBetween(
      @Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

  @Query("SELECT SUM(i.amount) FROM Income i WHERE i.incomeDate BETWEEN :start AND :end")
  Optional<BigDecimal> sumByDateBetween(
      @Param("start") LocalDate start, @Param("end") LocalDate end);
}
