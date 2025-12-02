package com.example.expense.service;

import com.example.expense.dto.DashboardSummaryResponse;
import com.example.expense.repository.BudgetRepository;
import com.example.expense.repository.ExpenseRepository;
import com.example.expense.repository.IncomeRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

  private final IncomeRepository incomeRepository;
  private final ExpenseRepository expenseRepository;
  private final BudgetRepository budgetRepository;

  public DashboardSummaryResponse getUserDashboard(Long userId) {
    LocalDate now = LocalDate.now();
    int month = now.getMonthValue();
    int year = now.getYear();

    BigDecimal totalIncome =
        incomeRepository
            .sumByUserAndDateBetween(
                userId, now.withDayOfMonth(1), now.withDayOfMonth(now.lengthOfMonth()))
            .orElse(BigDecimal.ZERO);

    BigDecimal totalExpense =
        expenseRepository
            .sumByUserAndDateBetween(
                userId, now.withDayOfMonth(1), now.withDayOfMonth(now.lengthOfMonth()))
            .orElse(BigDecimal.ZERO);

    BigDecimal totalBudget =
        budgetRepository.sumByUserAndPeriod(userId, month, year).orElse(BigDecimal.ZERO);

    BigDecimal balance = totalIncome.subtract(totalExpense);
    BigDecimal budgetUsage =
        totalBudget.compareTo(BigDecimal.ZERO) > 0
            ? totalExpense.divide(totalBudget, 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

    return new DashboardSummaryResponse(totalIncome, totalExpense, balance, budgetUsage);
  }

  public DashboardSummaryResponse getAdminDashboard() {
    LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
    LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

    BigDecimal totalIncome =
        incomeRepository.sumByDateBetween(startOfMonth, endOfMonth).orElse(BigDecimal.ZERO);

    BigDecimal totalExpense =
        expenseRepository.sumByDateBetween(startOfMonth, endOfMonth).orElse(BigDecimal.ZERO);

    BigDecimal totalBudget =
        budgetRepository.sumByPeriodBetween(startOfMonth, endOfMonth).orElse(BigDecimal.ZERO);

    BigDecimal balance = totalIncome.subtract(totalExpense);
    BigDecimal budgetUsage =
        totalBudget.compareTo(BigDecimal.ZERO) > 0
            ? totalExpense.divide(totalBudget, 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

    return new DashboardSummaryResponse(totalIncome, totalExpense, balance, budgetUsage);
  }
}
