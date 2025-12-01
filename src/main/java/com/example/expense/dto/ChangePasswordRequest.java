package com.example.expense.dto;

import com.example.expense.validator.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@PasswordMatches(passwordField = "newPassword", confirmPasswordField = "confirmNewPassword")
@Data
public class ChangePasswordRequest {
  @NotBlank private String oldPassword;

  @NotBlank
  @Size(min = 8, message = "New password must be at least 8 characters long")
  private String newPassword;

  @NotBlank
  @Size(min = 8, message = "Confirm password must be at least 8 characters long")
  private String confirmNewPassword;
}
