package com.example.expense.controller;

import com.example.expense.dto.ChangePasswordRequest;
import com.example.expense.dto.UserProfileResponse;
import com.example.expense.entity.User;
import com.example.expense.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping("/user/profile")
  public UserProfileResponse profile(Authentication authentication) {
    String email = authentication.getName();
    User user = userService.getByEmail(email);

    return new UserProfileResponse(user.getId(), user.getEmail(), user.getName());
  }

  @PostMapping("/user/change-password")
  public String changePassword(
      Authentication authentication, @RequestBody ChangePasswordRequest req) {

    String email = authentication.getName();
    User user = userService.getByEmail(email);
    userService.changePassword(user, req);

    return "Password updated successfully";
  }
}
