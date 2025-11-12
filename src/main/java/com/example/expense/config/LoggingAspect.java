package com.example.expense.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile("dev")
public class LoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

  @Pointcut(
      "execution(* com.example.demo.controller.*.*(..)) || execution(* com.example.demo.service.*.*(..))")
  public void applicationPackagePointcut() {}

  @Before("applicationPackagePointcut()")
  public void logBefore(JoinPoint joinPoint) {
    logger.info("Before method: {}", joinPoint.getSignature().toShortString());
  }

  @AfterReturning(pointcut = "applicationPackagePointcut()", returning = "result")
  public void logAfter(JoinPoint joinPoint, Object result) {
    logger.info("After method: {} - Return: {}", joinPoint.getSignature().toShortString(), result);
  }

  @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "error")
  public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
    logger.error(
        "Exception in {} - Message: {}",
        joinPoint.getSignature().toShortString(),
        error.getMessage());
  }
}
