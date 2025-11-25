package com.example.expense.service;

import com.example.expense.dto.UserForm;
import com.example.expense.entity.Role;
import com.example.expense.entity.User;
import com.example.expense.enums.RoleType;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.repository.RoleRepository;
import com.example.expense.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  public Page<User> searchByName(String keyword, Pageable pageable) {
    if (keyword == null || keyword.isBlank()) {
      return userRepository.findAll(pageable);
    }
    return userRepository.findByNameContainingIgnoreCase(keyword, pageable);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public User findById(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
  }

  public User create(UserForm form) {
    if (form.getRawPassword() == null || form.getRawPassword().isBlank()) {
      throw new IllegalArgumentException("Password cannot be empty");
    }

    User user = new User();
    user.setName(form.getName());
    user.setEmail(form.getEmail());
    user.setPasswordHash(passwordEncoder.encode(form.getRawPassword()));
    user.setUuid(UUID.randomUUID().toString());

    if (form.getRoleId() != null) {
      Role role =
          roleRepository
              .findById(form.getRoleId())
              .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
      user.setRole(role);
    } else {
      Role defaultRole =
          roleRepository
              .findByName(RoleType.USER)
              .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
      user.setRole(defaultRole);
    }

    user.setIsActive(form.isActive());
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());

    return userRepository.save(user);
  }

  public User update(Long id, UserForm form) {
    User user = findById(id);

    user.setName(form.getName());
    user.setEmail(user.getEmail());

    if (form.getRoleId() != null) {
      Role role =
          roleRepository
              .findById(form.getRoleId())
              .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
      user.setRole(role);
    }

    user.setIsActive(form.isActive());

    if (form.getRawPassword() != null && !form.getRawPassword().isBlank()) {
      user.setPasswordHash(passwordEncoder.encode(form.getRawPassword()));
    }
    user.setUpdatedAt(LocalDateTime.now());

    return userRepository.save(user);
  }

  public void delete(Long id) {
    userRepository.deleteById(id);
  }
}
