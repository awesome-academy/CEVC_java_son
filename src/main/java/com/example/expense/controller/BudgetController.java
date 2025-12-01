package com.example.expense.controller;

import com.example.expense.dto.BudgetRequest;
import com.example.expense.dto.BudgetResponse;
import com.example.expense.dto.CategoryResponse;
import com.example.expense.entity.Budget;
import com.example.expense.entity.Category;
import com.example.expense.entity.User;
import com.example.expense.exception.AccessDeniedException;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.service.BudgetService;
import com.example.expense.service.CategoryService;
import com.example.expense.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

  private final BudgetService budgetService;
  private final UserService userService;
  private final CategoryService categoryService;

  @GetMapping
  public Page<BudgetResponse> list(Pageable pageable, Authentication auth) {
    User user = userService.getByEmail(auth.getName());
    return budgetService.listBudgets(pageable, user).map(this::toResponse);
  }

  @GetMapping("/{id}")
  public BudgetResponse getById(@PathVariable Long id, Authentication auth) {
    Budget budget =
        budgetService
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
    User user = userService.getByEmail(auth.getName());
    if (!budget.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("not_allowed");
    }
    return toResponse(budget);
  }

  @PostMapping
  public BudgetResponse create(@RequestBody @Valid BudgetRequest request, Authentication auth) {
    User user = userService.getByEmail(auth.getName());

    Budget budget = toEntity(request);
    budget.setUser(user);

    Budget saved = budgetService.saveBudget(budget);
    return toResponse(saved);
  }

  @PutMapping("/{id}")
  public BudgetResponse update(
      @PathVariable Long id, @RequestBody @Valid BudgetRequest request, Authentication auth) {
    User user = userService.getByEmail(auth.getName());
    Budget existingBudget =
        budgetService
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
    if (!existingBudget.getUser().getId().equals(user.getId())) {
      throw new ResourceNotFoundException("Budget not found");
    }
    Budget budget = toEntity(request);
    budget.setId(id);
    budget.setUser(user);

    Budget updated = budgetService.updateBudget(budget);
    return toResponse(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
    User user = userService.getByEmail(auth.getName());
    Budget budget =
        budgetService
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));

    if (!budget.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("not_allowed");
    }

    budgetService.delete(id);
    return ResponseEntity.noContent().build();
  }

  private Budget toEntity(BudgetRequest req) {
    Budget b = new Budget();
    b.setAmountLimit(req.getAmountLimit());
    b.setPeriod(req.getPeriod());
    b.setPeriodType(req.getPeriodType());

    if (req.getCategoryId() != null) {
      Category c = categoryService.findById(req.getCategoryId());
      if (c == null) {
        throw new ResourceNotFoundException("error.model_not_found");
      }
      b.setCategory(c);
    }

    return b;
  }

  private BudgetResponse toResponse(Budget b) {
    BudgetResponse res = new BudgetResponse();
    res.setId(b.getId());
    res.setUuid(b.getUuid());
    res.setAmountLimit(b.getAmountLimit());
    res.setPeriod(b.getPeriod());
    res.setPeriodType(b.getPeriodType());
    res.setCreatedAt(b.getCreatedAt());
    res.setUpdatedAt(b.getUpdatedAt());
    res.setCategory(toCategoryResponse(b.getCategory()));

    return res;
  }

  private CategoryResponse toCategoryResponse(Category c) {
    if (c == null) return null;

    return new CategoryResponse(c.getId(), c.getName(), c.getType());
  }
}
