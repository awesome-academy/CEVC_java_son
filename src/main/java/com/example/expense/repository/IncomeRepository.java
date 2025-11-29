package com.example.expense.repository;

import com.example.expense.entity.Income;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income, Long> {
  Page<Income> findByTitleContainingIgnoreCase(String title, Pageable pageable);

  Page<Income> findByUserId(Long userId, Pageable pageable);
}
