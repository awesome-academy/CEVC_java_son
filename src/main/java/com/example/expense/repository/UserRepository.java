package com.example.expense.repository;

import com.example.expense.entity.User;
import com.example.expense.enums.RoleType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);

  List<User> findByRole_NameNot(RoleType roleName);
}
