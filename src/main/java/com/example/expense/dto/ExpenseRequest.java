package com.example.expense.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ExpenseRequest {
  private Long id;
  @NotBlank private String title;

  @NotNull private BigDecimal amount;

  @NotNull private LocalDate expenseDate;

  private Long categoryId;

  private String note;
}
