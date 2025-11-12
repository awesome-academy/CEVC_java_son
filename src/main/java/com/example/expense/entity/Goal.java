package com.example.expense.entity;

import com.example.expense.enums.GoalType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Goal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 36)
  @Column(nullable = false, unique = true, length = 36)
  private String uuid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonBackReference
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "goal_type", length = 20)
  private GoalType goalType;

  @NotBlank
  @Size(max = 150)
  private String name;

  @NotNull
  @Column(name = "target_amount", precision = 15, scale = 2)
  private BigDecimal targetAmount;

  @Column(name = "current_amount", precision = 15, scale = 2)
  private BigDecimal currentAmount;

  @Column(name = "period")
  private LocalDate period;

  private LocalDate deadline;

  private Boolean achieved = false;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
