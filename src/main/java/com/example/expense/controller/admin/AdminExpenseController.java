package com.example.expense.controller.admin;

import com.example.expense.entity.Expense;
import com.example.expense.enums.RoleType;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.repository.UserRepository;
import com.example.expense.service.ExpenseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/expenses")
public class AdminExpenseController {

  private final ExpenseService expenseService;
  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;

  @GetMapping
  public String listExpenses(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam(value = "q", required = false) String q,
      Model model) {

    Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Expense> expenses = expenseService.listExpenses(q, pageable);

    model.addAttribute("expenses", expenses.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalItems", expenses.getTotalElements());
    model.addAttribute("totalPages", expenses.getTotalPages());
    model.addAttribute("q", q);

    return "admin/expense/list";
  }

  @GetMapping("/add")
  public String addForm(Model model) {
    Expense expense = new Expense();
    expense.setUuid(UUID.randomUUID().toString());

    model.addAttribute("expense", expense);
    model.addAttribute("categories", categoryRepository.findAll());
    model.addAttribute("users", userRepository.findByRole_NameNot(RoleType.ADMIN));

    return "admin/expense/add";
  }

  @PostMapping("/add")
  public String add(@ModelAttribute Expense expense, RedirectAttributes redirectAttributes) {
    expenseService.saveExpense(expense);
    redirectAttributes.addFlashAttribute("success", "Expense created successfully");
    return "redirect:/admin/expenses";
  }

  @GetMapping("/edit/{id}")
  public String editForm(
      @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    Expense expense = expenseService.findById(id).orElse(null);

    if (expense == null) {
      redirectAttributes.addFlashAttribute("error", "Expense not found");
      return "redirect:/admin/expenses";
    }

    String formattedDate =
        expense.getExpenseDate() != null ? expense.getExpenseDate().toString() : "";
    model.addAttribute("expense", expense);
    model.addAttribute("formattedDate", formattedDate);
    model.addAttribute("categories", categoryRepository.findAll());
    model.addAttribute("users", userRepository.findByRole_NameNot(RoleType.ADMIN));

    return "admin/expense/edit";
  }

  @PostMapping("/edit")
  public String edit(@ModelAttribute Expense expense, RedirectAttributes redirectAttributes) {
    expenseService.updateExpense(expense);
    redirectAttributes.addFlashAttribute("success", "Expense updated successfully");
    return "redirect:/admin/expenses";
  }

  @GetMapping("/delete/{id}")
  public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    boolean deleted = expenseService.deleteById(id);
    if (deleted) {
      redirectAttributes.addFlashAttribute("success", "Expense deleted");
    } else {
      redirectAttributes.addFlashAttribute("error", "Expense not found");
    }
    return "redirect:/admin/expenses";
  }
}
