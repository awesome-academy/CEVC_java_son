package com.example.expense.dto;

import com.example.expense.validation.PasswordMatches;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class RegisterRequest {

  @NotBlank @Email private String email;

  @NotBlank private String password;

  @NotBlank private String confirmPassword;

  @NotBlank private String name;
}
