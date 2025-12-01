package com.example.expense.dto;

import com.example.expense.enums.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryResponse {
  private Long id;
  private String name;
  private CategoryType type;
}
