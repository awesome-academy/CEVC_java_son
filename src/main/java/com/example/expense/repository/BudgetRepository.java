package com.example.expense.repository;

import com.example.expense.entity.Budget;
import com.example.expense.entity.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
  Page<Budget> findByUser(User user, Pageable pageable);

  @Query(
      """
        SELECT SUM(b.amountLimit)
        FROM Budget b
        WHERE b.user.id = :userId
          AND FUNCTION('YEAR', b.period) = :year
          AND FUNCTION('MONTH', b.period) = :month
    """)
  Optional<BigDecimal> sumByUserAndPeriod(
      @Param("userId") Long userId, @Param("month") int month, @Param("year") int year);

  @Query(
      """
    SELECT SUM(b.amountLimit)
    FROM Budget b
    WHERE b.period BETWEEN :start AND :end
    """)
  Optional<BigDecimal> sumByPeriodBetween(
      @Param("start") LocalDate start, @Param("end") LocalDate end);

  List<Budget> findByUserId(Long userId);
}
