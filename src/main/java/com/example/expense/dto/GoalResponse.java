package com.example.expense.dto;

import com.example.expense.enums.GoalType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoalResponse {
  private Long id;
  private String uuid;
  private String name;
  private GoalType goalType;
  private BigDecimal targetAmount;
  private BigDecimal currentAmount;
  private LocalDate period;
  private LocalDate deadline;
  private Boolean achieved;
}
