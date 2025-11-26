package com.example.expense.dto;

import com.example.expense.validation.PasswordMatches;
import lombok.Data;

@Data
@PasswordMatches
public class ChangePasswordRequest {
  private String oldPassword;
  private String newPassword;
  private String confirmNewPassword;
}
