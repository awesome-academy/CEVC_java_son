package com.example.expense.controller.admin;

import com.example.expense.config.CustomUserDetailsService;
import com.example.expense.entity.Category;
import com.example.expense.entity.User;
import com.example.expense.enums.CategoryType;
import com.example.expense.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {

  private final CategoryService categoryService;
  private final CustomUserDetailsService customUserDetailsService;

  @GetMapping
  public String listCategories(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam(value = "q", required = false) String q,
      Model model) {
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Category> categories = categoryService.searchByName(q, pageable);

    model.addAttribute("categories", categories.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalItems", categories.getTotalElements());
    model.addAttribute("totalPages", categories.getTotalPages());
    model.addAttribute("q", q);
    return "admin/category/list";
  }

  @GetMapping("/add")
  public String addForm(Model model) {
    model.addAttribute("category", new Category());
    model.addAttribute("types", CategoryType.values());
    return "admin/category/add";
  }

  @PostMapping("/add")
  public String add(@ModelAttribute Category category) {
    User user = customUserDetailsService.getCurrentUser();
    categoryService.create(category, user.getId());

    return "redirect:/admin/categories";
  }

  @GetMapping("/edit/{id}")
  public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("category", categoryService.findById(id));
    model.addAttribute("types", CategoryType.values());
    return "admin/category/edit";
  }

  @PostMapping("/edit")
  public String edit(@ModelAttribute Category category) {
    User user = customUserDetailsService.getCurrentUser();
    categoryService.update(category, user.getId());

    return "redirect:/admin/categories";
  }

  @GetMapping("/delete/{id}")
  public String delete(@PathVariable Long id) {
    categoryService.delete(id);
    return "redirect:/admin/categories";
  }
}
