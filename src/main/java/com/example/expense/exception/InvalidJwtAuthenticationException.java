package com.example.expense.exception;

public class InvalidJwtAuthenticationException extends RuntimeException {
  public InvalidJwtAuthenticationException(String message) {
    super(message);
  }
}
