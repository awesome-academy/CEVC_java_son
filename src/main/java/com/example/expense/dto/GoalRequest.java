package com.example.expense.dto;

import com.example.expense.enums.GoalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class GoalRequest {

  @NotBlank private String name;

  @NotNull private GoalType goalType;

  @NotNull private BigDecimal targetAmount;

  private LocalDate period;

  private LocalDate deadline;
}
