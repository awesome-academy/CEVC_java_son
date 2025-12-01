package com.example.expense.repository;

import com.example.expense.entity.Category;
import com.example.expense.enums.CategoryType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

  List<Category> findByType(CategoryType type);
}
