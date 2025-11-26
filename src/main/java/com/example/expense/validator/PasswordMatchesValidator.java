package com.example.expense.validation;

import com.example.expense.dto.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
    implements ConstraintValidator<PasswordMatches, RegisterRequest> {

  @Override
  public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
    if (request == null) return true;

    return request.getPassword() != null
        && request.getPassword().equals(request.getConfirmPassword());
  }
}
