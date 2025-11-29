package com.example.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseResponse {
  private Long id;
  private String title;
  private BigDecimal amount;
  private LocalDate expenseDate;
  private String note;
  private String categoryName;
}
