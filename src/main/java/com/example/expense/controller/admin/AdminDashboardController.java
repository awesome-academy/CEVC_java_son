package com.example.expense.controller.admin;

import com.example.expense.dto.DashboardSummaryResponse;
import com.example.expense.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

  private final DashboardService dashboardService;

  public AdminDashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/admin/dashboard")
  public String dashboard(Model model) {
    DashboardSummaryResponse summary = dashboardService.getAdminDashboard();
    model.addAttribute("summary", summary);

    return "admin/dashboard";
  }
}
