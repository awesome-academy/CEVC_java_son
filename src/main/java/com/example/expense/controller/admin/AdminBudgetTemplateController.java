package com.example.expense.controller.admin;

import com.example.expense.entity.BudgetTemplate;
import com.example.expense.enums.RoleType;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.repository.UserRepository;
import com.example.expense.service.BudgetTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/budget-templates")
public class AdminBudgetTemplateController {

  private final BudgetTemplateService budgetTemplateService;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;

  @GetMapping
  public String listTemplates(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam(value = "q", required = false) String q,
      Model model) {

    Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<BudgetTemplate> templates = budgetTemplateService.listTemplates(q, pageable);

    model.addAttribute("templates", templates.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalItems", templates.getTotalElements());
    model.addAttribute("totalPages", templates.getTotalPages());
    model.addAttribute("q", q);

    return "admin/budget_template/list";
  }

  @GetMapping("/add")
  public String addForm(Model model) {

    BudgetTemplate template = new BudgetTemplate();

    model.addAttribute("template", template);
    model.addAttribute("users", userRepository.findByRole_NameNot(RoleType.ADMIN));
    model.addAttribute("categories", categoryRepository.findAll());

    return "admin/budget_template/add";
  }

  @PostMapping("/add")
  public String add(
      @ModelAttribute BudgetTemplate template, RedirectAttributes redirectAttributes) {

    budgetTemplateService.saveTemplate(template);

    redirectAttributes.addFlashAttribute("success", "Budget template created successfully!");

    return "redirect:/admin/budget-templates";
  }

  @GetMapping("/edit/{id}")
  public String editForm(
      @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {

    BudgetTemplate template = budgetTemplateService.findById(id).orElse(null);

    if (template == null) {
      redirectAttributes.addFlashAttribute("error", "Budget template not found");
      return "redirect:/admin/budget-templates";
    }

    model.addAttribute("template", template);
    model.addAttribute("users", userRepository.findByRole_NameNot(RoleType.ADMIN));
    model.addAttribute("categories", categoryRepository.findAll());

    return "admin/budget_template/edit";
  }

  @PostMapping("/edit")
  public String edit(
      @ModelAttribute BudgetTemplate template, RedirectAttributes redirectAttributes) {

    budgetTemplateService.updateTemplate(template);

    redirectAttributes.addFlashAttribute("success", "Budget template updated successfully!");

    return "redirect:/admin/budget-templates";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {

    boolean deleted = budgetTemplateService.deleteById(id);

    if (deleted) {
      redirectAttributes.addFlashAttribute("success", "Budget template deleted!");
    } else {
      redirectAttributes.addFlashAttribute("error", "Budget template not found");
    }

    return "redirect:/admin/budget-templates";
  }
}
