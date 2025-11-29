package com.example.expense.service;

import com.example.expense.dto.BudgetTemplateResponse;
import com.example.expense.dto.CategoryResponse;
import com.example.expense.entity.BudgetTemplate;
import com.example.expense.entity.Category;
import com.example.expense.repository.BudgetTemplateRepository;
import com.example.expense.repository.CategoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class ReferenceDataService {

  private final CategoryRepository categoryRepository;
  private final BudgetTemplateRepository budgetTemplateRepository;

  public List<CategoryResponse> getAllCategories() {
    List<Category> categories = categoryRepository.findAll();
    return categories.stream()
        .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getType()))
        .collect(Collectors.toList());
  }

  public List<BudgetTemplateResponse> getAllBudgetTemplates() {
    List<BudgetTemplate> templates = budgetTemplateRepository.findAll();
    return templates.stream()
        .map(t -> new BudgetTemplateResponse(t.getId(), t.getName(), t.getDefaultAmount()))
        .collect(Collectors.toList());
  }
}
