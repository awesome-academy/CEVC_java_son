package com.example.expense.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardSummaryResponse {
  private BigDecimal totalIncome;
  private BigDecimal totalExpense;
  private BigDecimal balance;
  private BigDecimal budgetUsage;
}
