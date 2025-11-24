package com.example.expense.controller.admin;

import com.example.expense.entity.Income;
import com.example.expense.enums.RoleType;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.repository.UserRepository;
import com.example.expense.service.IncomeService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/incomes")
public class AdminIncomeController {

  private final IncomeService incomeService;
  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;

  @GetMapping
  public String listIncomes(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam(value = "q", required = false) String q,
      Model model) {
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Income> incomes = incomeService.listIncomes(q, pageable);

    model.addAttribute("incomes", incomes.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalItems", incomes.getTotalElements());
    model.addAttribute("totalPages", incomes.getTotalPages());
    model.addAttribute("q", q);

    return "admin/income/list";
  }

  @GetMapping("/add")
  public String addForm(Model model) {
    Income income = new Income();

    model.addAttribute("income", income);
    model.addAttribute("categories", categoryRepository.findAll());
    model.addAttribute("users", userRepository.findByRole_NameNot(RoleType.ADMIN));

    return "admin/income/add";
  }

  @PostMapping("/add")
  public String add(
      @ModelAttribute Income income,
      @RequestParam(value = "attachmentFiles", required = false)
          List<MultipartFile> attachmentFiles,
      RedirectAttributes redirectAttributes)
      throws IOException {

    incomeService.saveIncome(income, attachmentFiles);

    redirectAttributes.addFlashAttribute("success", "Income created successfully");
    return "redirect:/admin/incomes";
  }

  @GetMapping("/edit/{id}")
  public String editForm(
      @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    Income income = incomeService.findById(id).orElse(null);

    if (income == null) {
      redirectAttributes.addFlashAttribute("error", "Income not found");
      return "redirect:/admin/incomes";
    }

    String formattedDate = income.getIncomeDate() != null ? income.getIncomeDate().toString() : "";

    model.addAttribute("income", income);
    model.addAttribute("formattedDate", formattedDate);
    model.addAttribute("categories", categoryRepository.findAll());
    model.addAttribute("users", userRepository.findByRole_NameNot(RoleType.ADMIN));

    return "admin/income/edit";
  }

  @PostMapping("/edit")
  public String edit(
      @ModelAttribute Income income,
      @RequestParam(value = "attachmentFiles", required = false)
          List<MultipartFile> attachmentFiles,
      RedirectAttributes redirectAttributes) {

    incomeService.updateIncome(income, attachmentFiles);

    redirectAttributes.addFlashAttribute("success", "Income updated successfully");
    return "redirect:/admin/incomes";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    boolean deleted = incomeService.deleteById(id);

    if (deleted) {
      redirectAttributes.addFlashAttribute("success", "Income deleted");
    } else {
      redirectAttributes.addFlashAttribute("error", "Income not found");
    }

    return "redirect:/admin/incomes";
  }
}
