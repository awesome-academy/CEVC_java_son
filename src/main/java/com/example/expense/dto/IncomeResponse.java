package com.example.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IncomeResponse {
  private Long id;
  private String title;
  private BigDecimal amount;
  private LocalDate incomeDate;
  private String note;
  private String categoryName;
}
