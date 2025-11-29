package com.example.expense.controller;

import com.example.expense.dto.BudgetTemplateResponse;
import com.example.expense.dto.CategoryResponse;
import com.example.expense.enums.CategoryType;
import com.example.expense.service.ReferenceDataService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReferenceDataController {
  private final ReferenceDataService referenceDataService;

  @GetMapping("/data/categories")
  public List<CategoryResponse> getCategories(
      @RequestParam(value = "type", required = false) CategoryType type) {
    if (type == null || (!type.equals(CategoryType.INCOME) && !type.equals(CategoryType.EXPENSE))) {
      type = CategoryType.EXPENSE;
    }

    return referenceDataService.getCategoriesByType(type);
  }

  @GetMapping("/data/budget-templates")
  public List<BudgetTemplateResponse> getBudgetTemplates() {
    return referenceDataService.getAllBudgetTemplates();
  }
}
