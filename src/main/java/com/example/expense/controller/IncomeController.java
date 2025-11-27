package com.example.expense.controller;

import com.example.expense.dto.IncomeRequest;
import com.example.expense.dto.IncomeResponse;
import com.example.expense.entity.Category;
import com.example.expense.entity.Income;
import com.example.expense.entity.User;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.service.IncomeService;
import com.example.expense.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
public class IncomeController {

  private final IncomeService incomeService;
  private final UserService userService;
  private final CategoryRepository categoryRepository;

  private IncomeResponse mapToResponse(Income income) {
    String categoryName = income.getCategory() != null ? income.getCategory().getName() : null;
    return new IncomeResponse(
        income.getId(),
        income.getTitle(),
        income.getAmount(),
        income.getIncomeDate(),
        income.getNote(),
        categoryName);
  }

  @GetMapping
  public List<IncomeResponse> listIncomes(
      Authentication auth,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    User user = userService.getByEmail(auth.getName());
    Page<Income> incomes = incomeService.listIncomes(null, PageRequest.of(page, size));
    // filter only incomes of this user
    List<Income> userIncomes =
        incomes.getContent().stream()
            .filter(i -> i.getUser().getId().equals(user.getId()))
            .collect(Collectors.toList());

    return userIncomes.stream().map(this::mapToResponse).collect(Collectors.toList());
  }

  @PostMapping
  public IncomeResponse createIncome(
      @RequestPart("income") @Valid IncomeRequest request,
      @RequestPart(value = "files", required = false) List<MultipartFile> files,
      Authentication auth) {
    User user = userService.getByEmail(auth.getName());

    Income income = new Income();
    income.setTitle(request.getTitle());
    income.setAmount(request.getAmount());
    income.setIncomeDate(request.getIncomeDate());
    income.setNote(request.getNote());
    income.setUser(user);
    if (request.getCategoryId() != null) {
      Category category =
          categoryRepository
              .findById(request.getCategoryId())
              .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
      income.setCategory(category);
    }

    Income saved = incomeService.saveIncome(income, files);
    return mapToResponse(saved);
  }

  @PutMapping("/{id}")
  public IncomeResponse updateIncome(
      @PathVariable Long id,
      @RequestPart("income") @Valid IncomeRequest request,
      @RequestPart(value = "files", required = false) List<MultipartFile> files,
      Authentication auth) {
    User user = userService.getByEmail(auth.getName());

    Income income = new Income();
    income.setId(id);
    income.setTitle(request.getTitle());
    income.setAmount(request.getAmount());
    income.setIncomeDate(request.getIncomeDate());
    income.setNote(request.getNote());
    income.setUser(user);
    if (request.getCategoryId() != null) {
      Category category =
          categoryRepository
              .findById(request.getCategoryId())
              .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
      income.setCategory(category);
    }

    Income updated = incomeService.updateIncome(income, files);
    return mapToResponse(updated);
  }

  @DeleteMapping("/{id}")
  public void deleteIncome(@PathVariable Long id, Authentication auth) {
    User user = userService.getByEmail(auth.getName());
    Income income =
        incomeService.findById(id).orElseThrow(() -> new RuntimeException("income.not_found"));

    if (!income.getUser().getId().equals(user.getId())) {
      throw new RuntimeException("not_allowed");
    }

    incomeService.deleteById(id);
  }
}
