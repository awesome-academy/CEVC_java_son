package com.example.expense.controller;

import com.example.expense.dto.BudgetRequest;
import com.example.expense.dto.BudgetResponse;
import com.example.expense.entity.Budget;
import com.example.expense.entity.User;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.service.BudgetService;
import com.example.expense.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

  private final BudgetService budgetService;
  private final UserService userService;

  @GetMapping
  public Page<BudgetResponse> list(Pageable pageable, Authentication auth) {
    User user = userService.getByEmail(auth.getName());
    return budgetService.listBudgets(pageable, user).map(this::toResponse);
  }

  @GetMapping("/{id}")
  public BudgetResponse getById(@PathVariable Long id) {
    Budget budget =
        budgetService
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
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
  public BudgetResponse update(@PathVariable Long id, @RequestBody @Valid BudgetRequest request) {

    Budget budget = toEntity(request);
    budget.setId(id);

    Budget updated = budgetService.updateBudget(budget);
    return toResponse(updated);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    budgetService.delete(id);
  }

  private Budget toEntity(BudgetRequest req) {
    Budget b = new Budget();
    b.setAmountLimit(req.getAmountLimit());
    b.setPeriod(req.getPeriod());
    b.setPeriodType(req.getPeriodType());
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
    return res;
  }
}
