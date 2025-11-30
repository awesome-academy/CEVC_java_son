package com.example.expense.controller;

import com.example.expense.dto.DashboardSummaryResponse;
import com.example.expense.entity.User;
import com.example.expense.service.DashboardService;
import com.example.expense.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {
  private final UserService userService;
  private final DashboardService dashboardService;

  @GetMapping("/dashboard")
  public DashboardSummaryResponse getDashboard(Authentication auth) {
    User user = userService.getByEmail(auth.getName());
    return dashboardService.getUserDashboard(user.getId());
  }
}
