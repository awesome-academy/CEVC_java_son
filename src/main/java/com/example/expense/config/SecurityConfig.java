package com.example.expense.config;

import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(2)
public class SecurityConfig {

  private final CustomUserDetailsService userDetailsService;

  public SecurityConfig(CustomUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {

    http.securityMatcher("/user/**", "/login", "/logout", "/register")
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/login", "/register")
                    .permitAll()
                    .requestMatchers("/user/**")
                    .hasRole("USER")
                    .anyRequest()
                    .authenticated())
        .formLogin(
            form -> form.loginPage("/login").defaultSuccessUrl("/user/dashboard", true).permitAll())
        .logout(
            logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll());

    return http.build();
  }
}
