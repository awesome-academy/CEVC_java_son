package com.example.expense.service;

import com.example.expense.entity.Category;
import com.example.expense.entity.Expense;
import com.example.expense.entity.User;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.repository.ExpenseRepository;
import com.example.expense.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;

  public Page<Expense> listExpenses(String keyword, Pageable pageable) {
    if (keyword == null || keyword.isBlank()) {
      return expenseRepository.findAll(pageable);
    }
    return expenseRepository.findByTitleContainingIgnoreCase(keyword, pageable);
  }

  public Optional<Expense> findById(Long id) {
    return expenseRepository.findById(id);
  }

  public Expense saveExpense(Expense expense) {
    if (expense.getUser() == null || expense.getUser().getId() == null) {
      throw new ResourceNotFoundException("User is required");
    }
    User user =
        userRepository
            .findById(expense.getUser().getId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    expense.setUser(user);

    if (expense.getCategory() != null && expense.getCategory().getId() != null) {
      Category category =
          categoryRepository
              .findById(expense.getCategory().getId())
              .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
      expense.setCategory(category);
    } else {
      expense.setCategory(null);
    }

    if (expense.getUuid() == null || expense.getUuid().isEmpty()) {
      expense.setUuid(UUID.randomUUID().toString());
    }

    if (expense.getExpenseDate() == null) {
      expense.setExpenseDate(LocalDate.now());
    }

    return expenseRepository.save(expense);
  }

  public Expense updateExpense(Expense expense) {
    if (expense.getId() == null) {
      throw new ResourceNotFoundException("Invalid Expense ID");
    }

    Expense existing =
        expenseRepository
            .findById(expense.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

    existing.setTitle(expense.getTitle());
    existing.setAmount(expense.getAmount());
    existing.setExpenseDate(expense.getExpenseDate());
    existing.setNote(expense.getNote());

    if (expense.getUser() != null && expense.getUser().getId() != null) {
      User user =
          userRepository
              .findById(expense.getUser().getId())
              .orElseThrow(() -> new ResourceNotFoundException("User not found"));
      existing.setUser(user);
    }

    if (expense.getCategory() != null && expense.getCategory().getId() != null) {
      Category category =
          categoryRepository
              .findById(expense.getCategory().getId())
              .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
      existing.setCategory(category);
    } else {
      existing.setCategory(null);
    }

    return expenseRepository.save(existing);
  }

  public boolean deleteById(Long id) {
    if (expenseRepository.existsById(id)) {
      expenseRepository.deleteById(id);
      return true;
    }
    return false;
  }

  public List<Expense> findAll() {
    return expenseRepository.findAll();
  }
}
