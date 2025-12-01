package com.example.expense.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
  private String accessToken;
  private String tokenType;
}
