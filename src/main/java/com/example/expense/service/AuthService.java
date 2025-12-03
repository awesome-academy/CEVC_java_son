package com.example.expense.service;

import com.example.expense.dto.LoginRequest;
import com.example.expense.dto.LoginResponse;
import com.example.expense.dto.RegisterRequest;
import com.example.expense.entity.Role;
import com.example.expense.entity.User;
import com.example.expense.enums.RoleType;
import com.example.expense.exception.AuthenticationFailedException;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.repository.RoleRepository;
import com.example.expense.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public LoginResponse login(LoginRequest request) {
    User user =
        userRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new AuthenticationFailedException("auth.wrong_email_or_password"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
      throw new AuthenticationFailedException("auth.wrong_email_or_password");
    }

    String token = jwtService.generateToken(user);
    return new LoginResponse(token, "Bearer");
  }

  public User register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("auth.email_already_exists");
    }

    User user = new User();
    user.setUuid(UUID.randomUUID().toString());
    user.setEmail(request.getEmail());
    user.setName(request.getName());
    user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

    user.setIsActive(true);
    Role defaultRole =
        roleRepository
            .findByName(RoleType.USER)
            .orElseThrow(() -> new ResourceNotFoundException("error.model_not_found"));
    user.setRole(defaultRole);

    return userRepository.save(user);
  }
}
