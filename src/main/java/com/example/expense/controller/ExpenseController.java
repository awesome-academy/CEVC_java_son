package com.example.expense.controller;

import com.example.expense.dto.ExpenseRequest;
import com.example.expense.dto.ExpenseResponse;
import com.example.expense.entity.Category;
import com.example.expense.entity.Expense;
import com.example.expense.entity.User;
import com.example.expense.exception.AccessDeniedException;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.service.ExpenseService;
import com.example.expense.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

  private final ExpenseService expenseService;
  private final UserService userService;
  private final CategoryRepository categoryRepository;

  private ExpenseResponse mapToResponse(Expense expense) {
    String categoryName = expense.getCategory() != null ? expense.getCategory().getName() : null;
    return new ExpenseResponse(
        expense.getId(),
        expense.getTitle(),
        expense.getAmount(),
        expense.getExpenseDate(),
        expense.getNote(),
        categoryName);
  }

  @GetMapping
  public List<ExpenseResponse> listExpenses(Authentication auth) {
    User user = userService.getByEmail(auth.getName());
    List<Expense> expenses = expenseService.findByUserId(user.getId());

    return expenses.stream().map(this::mapToResponse).collect(Collectors.toList());
  }

  @PostMapping
  public ExpenseResponse createExpense(
      @RequestPart("expense") @Valid ExpenseRequest request,
      @RequestPart(value = "files", required = false) List<MultipartFile> files,
      Authentication auth) {

    User user = userService.getByEmail(auth.getName());
    Expense expense = mapToEntity(request, user, null);
    Expense saved = expenseService.saveExpense(expense, files);
    return mapToResponse(saved);
  }

  @PutMapping("/{id}")
  public ExpenseResponse updateExpense(
      @PathVariable Long id,
      @RequestPart("expense") @Valid ExpenseRequest request,
      @RequestPart(value = "files", required = false) List<MultipartFile> files,
      Authentication auth) {

    User user = userService.getByEmail(auth.getName());
    Expense existing =
        expenseService
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));

    if (!existing.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("not_allowed");
    }

    Expense expense = mapToEntity(request, user, id);
    Expense updated = expenseService.updateExpense(expense, files);
    return mapToResponse(updated);
  }

  @DeleteMapping("/{id}")
  public void deleteExpense(@PathVariable Long id, Authentication auth) {
    User user = userService.getByEmail(auth.getName());
    Expense expense =
        expenseService
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));

    if (!expense.getUser().getId().equals(user.getId())) {
      throw new AccessDeniedException("not_allowed");
    }

    expenseService.deleteById(id);
  }

  private Expense mapToEntity(ExpenseRequest request, User user, Long id) {
    Expense expense = new Expense();
    if (id != null) {
      expense.setId(id);
    }
    expense.setTitle(request.getTitle());
    expense.setAmount(request.getAmount());
    expense.setExpenseDate(request.getExpenseDate());
    expense.setNote(request.getNote());
    expense.setUser(user);

    if (request.getCategoryId() != null) {
      Category category =
          categoryRepository
              .findById(request.getCategoryId())
              .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
      expense.setCategory(category);
    }
    return expense;
  }
}
