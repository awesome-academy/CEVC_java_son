package com.example.expense.controller;

import com.example.expense.dto.BudgetTemplateResponse;
import com.example.expense.dto.CategoryResponse;
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
  public List<CategoryResponse> getCategories() {
    return referenceDataService.getAllCategories();
  }

  @GetMapping("/data/budget-templates")
  public List<BudgetTemplateResponse> getBudgetTemplates() {
    return referenceDataService.getAllBudgetTemplates();
  }
}
