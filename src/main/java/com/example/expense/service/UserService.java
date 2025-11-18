package com.example.expense.service;

import com.example.expense.entity.Role;
import com.example.expense.entity.User;
import com.example.expense.enums.RoleType;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.repository.RoleRepository;
import com.example.expense.repository.UserRepository;
import java.util.List;
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

  public User create(User user) {
    user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

    if (user.getRole() == null) {
      Role defaultRole =
          roleRepository
              .findByName(RoleType.USER)
              .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
      user.setRole(defaultRole);
    }

    return userRepository.save(user);
  }

  public User update(Long id, User updated) {
    User user = findById(id);

    user.setName(updated.getName());
    user.setEmail(updated.getEmail());
    user.setRole(updated.getRole());
    user.setIsActive(updated.getIsActive());

    return userRepository.save(user);
  }

  public void delete(Long id) {
    userRepository.deleteById(id);
  }
}
