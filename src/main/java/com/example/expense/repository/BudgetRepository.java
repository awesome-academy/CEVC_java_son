package com.example.expense.repository;

import com.example.expense.entity.Budget;
import com.example.expense.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
  Page<Budget> findByUser(User user, Pageable pageable);
}
