package com.example.expense.validator;

import com.example.expense.dto.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
    implements ConstraintValidator<PasswordMatches, RegisterRequest> {

  @Override
  public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
    if (request == null) return true;

    boolean valid =
        request.getPassword() != null && request.getPassword().equals(request.getConfirmPassword());

    if (!valid) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate("Passwords do not match")
          .addPropertyNode("confirmPassword")
          .addConstraintViolation();
    }

    return valid;
  }
}
