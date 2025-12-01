package com.example.expense.dto;

import com.example.expense.entity.Budget;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BudgetCalcResult {
  private Budget budget;
  private BigDecimal currentAmount;
  private boolean exceeded;
}
