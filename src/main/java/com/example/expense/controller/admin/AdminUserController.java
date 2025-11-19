package com.example.expense.controller.admin;

import com.example.expense.config.CustomUserDetailsService;
import com.example.expense.entity.User;
import com.example.expense.repository.RoleRepository;
import com.example.expense.service.UserService;
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
@RequestMapping("/admin/users")
public class AdminUserController {

  private final UserService userService;
  private final RoleRepository roleRepository;
  private final CustomUserDetailsService customUserDetailsService;

  @GetMapping
  public String listUsers(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "size", defaultValue = "10") int size,
      @RequestParam(value = "q", required = false) String q,
      Model model) {
    Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<User> users = userService.searchByName(q, pageable);

    model.addAttribute("users", users.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("totalItems", users.getTotalElements());
    model.addAttribute("totalPages", users.getTotalPages());
    model.addAttribute("q", q);
    return "admin/user/list";
  }

  @GetMapping("/add")
  public String addForm(Model model) {
    model.addAttribute("user", new User());
    model.addAttribute("roles", roleRepository.findAll());
    return "admin/user/add";
  }

  @PostMapping("/add")
  public String add(@ModelAttribute User user) {
    userService.create(user);

    return "redirect:/admin/users";
  }

  @GetMapping("/edit/{id}")
  public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("user", userService.findById(id));
    model.addAttribute("roles", roleRepository.findAll());
    return "admin/user/edit";
  }

  @PostMapping("/edit/{id}")
  public String update(@PathVariable Long id, @ModelAttribute User user) {
    userService.update(id, user);
    return "redirect:/admin/users";
  }

  @GetMapping("/delete/{id}")
  public String delete(@PathVariable Long id) {
    userService.delete(id);
    return "redirect:/admin/users";
  }
}
