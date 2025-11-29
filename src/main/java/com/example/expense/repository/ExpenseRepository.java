package com.example.expense.repository;

import com.example.expense.entity.Expense;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
  Page<Expense> findByTitleContainingIgnoreCase(String title, Pageable pageable);

  List<Expense> findByUserId(Long userId);
}
