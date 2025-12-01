package com.example.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
  private Long id;
  private String uuid;
  private String message;
  private String type;
  private String sourceEntity;
  private Long sourceId;
  private Boolean isRead;
}
