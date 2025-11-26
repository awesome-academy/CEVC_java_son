package com.example.expense.repository;

import com.example.expense.entity.BudgetTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetTemplateRepository extends JpaRepository<BudgetTemplate, Long> {
  Page<BudgetTemplate> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
