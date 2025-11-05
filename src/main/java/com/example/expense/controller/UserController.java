package com.example.expense.controller;

import com.example.expense.entity.User;
import com.example.expense.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("user", new User());
        return "users/form";
    }

    @PostMapping
    public String save(@ModelAttribute User user) {
        userService.create(user);
        return "redirect:/users";
    }
}
