package com.example.expense.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class IncomeRequest {
  @NotBlank private String title;

  @NotNull private BigDecimal amount;

  @NotNull private LocalDate incomeDate;

  private Long categoryId;

  private String note;
}
