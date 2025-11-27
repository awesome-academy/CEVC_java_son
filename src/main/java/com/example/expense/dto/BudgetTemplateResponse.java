package com.example.expense.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BudgetTemplateResponse {
  private Long id;
  private String name;
  private BigDecimal defaultAmount;
}
