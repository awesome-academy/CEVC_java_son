package com.example.expense.dto;

import com.example.expense.enums.PeriodType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BudgetResponse {
  private Long id;
  private String uuid;
  private BigDecimal amountLimit;
  private LocalDate period;
  private PeriodType periodType;
  private CategoryResponse category;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
