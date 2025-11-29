package com.example.expense.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
  private Long id;
  private String email;
  private String name;
}
