package com.example.expense.service;

import com.example.expense.entity.Category;
import com.example.expense.entity.User;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;

  public Page<Category> searchByName(String keyword, Pageable pageable) {
    if (keyword == null || keyword.isBlank()) {
      return categoryRepository.findAll(pageable);
    }
    return categoryRepository.findByNameContainingIgnoreCase(keyword, pageable);
  }

  public List<Category> findAll() {
    return categoryRepository.findAll();
  }

  public Category findById(Long id) {
    return categoryRepository.findById(id).orElse(null);
  }

  public Category create(Category category, Long userId) {
    User user = User.builder().id(userId).build();

    category.setUuid(UUID.randomUUID().toString());
    category.setCreatedBy(user);
    category.setUpdatedBy(user);
    category.setCreatedAt(LocalDateTime.now());
    category.setUpdatedAt(LocalDateTime.now());
    return categoryRepository.save(category);
  }

  public Category update(Category category, Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));

    Category existing =
        categoryRepository
            .findById(category.getId())
            .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));

    existing.setName(category.getName());
    existing.setDescription(category.getDescription());
    existing.setType(category.getType());
    existing.setIcon(category.getIcon());

    existing.setUpdatedBy(user);
    existing.setUpdatedAt(LocalDateTime.now());

    return categoryRepository.save(existing);
  }

  public void delete(Long id) {
    categoryRepository.deleteById(id);
  }
}
