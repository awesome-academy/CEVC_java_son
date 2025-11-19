package com.example.expense.dto;

import lombok.Data;

@Data
public class UserForm {
  private Long id;
  private String name;
  private String email;
  private String rawPassword;
  private Long roleId;
  private boolean active;
}
