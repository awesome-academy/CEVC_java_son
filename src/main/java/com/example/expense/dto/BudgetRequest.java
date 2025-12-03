package com.example.expense.dto;

import com.example.expense.enums.PeriodType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BudgetRequest {

  private Long id;

  @NotNull private BigDecimal amountLimit;

  @NotNull private LocalDate period;

  @NotNull private PeriodType periodType;

  @NotNull private Long categoryId;
}
