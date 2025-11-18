package com.example.expense.config;

import com.example.expense.entity.Role;
import com.example.expense.entity.User;
import com.example.expense.enums.RoleType;
import com.example.expense.repository.RoleRepository;
import com.example.expense.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    Role adminRole =
        roleRepository
            .findByName(RoleType.ADMIN)
            .orElseGet(
                () ->
                    roleRepository.save(
                        Role.builder()
                            .uuid(UUID.randomUUID().toString())
                            .name(RoleType.ADMIN)
                            .description("Administrator")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build()));

    Role userRole =
        roleRepository
            .findByName(RoleType.USER)
            .orElseGet(
                () ->
                    roleRepository.save(
                        Role.builder()
                            .uuid(UUID.randomUUID().toString())
                            .name(RoleType.USER)
                            .description("Regular user")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build()));

    if (userRepository.findByEmail("admin@expense.com").isEmpty()) {
      userRepository.save(
          User.builder()
              .uuid(UUID.randomUUID().toString())
              .name("System Admin")
              .email("admin@expense.com")
              .passwordHash(passwordEncoder.encode("Aa@123456"))
              .role(adminRole)
              .isActive(true)
              .build());
    }
  }
}
